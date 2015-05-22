// Copyright (c) 2014, German Neuroinformatics Node (G-Node)
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted under the terms of the BSD License. See
// LICENSE file in the root of the Project.

package gndata.app.ui.metadata;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.*;
import gndata.app.html.PageCtrl;
import gndata.app.state.*;
import gndata.app.ui.util.listener.ChangeStatementListListener;
import gndata.app.ui.util.*;
import gndata.lib.util.OntologyHelper;

/**
 * Controller for viewing and editing DataProperties.
 */
public class MetadataDetailsCtrl extends PageCtrl {

    @FXML
    private TogglePane togglePane;
    @FXML
    private WebView webView;

    private MetadataNavState metadataState;
    private ProjectState projectState;

    private final StringProperty notificationMsg;

    private ObservableList<StatementTableItem> statementList;
    private ChangeStatementListListener listListener;

    private final StringProperty newPredValue;
    private ObjectProperty<RDFDatatype> newPredType;
    private StringProperty newPredPromptText;

    private ObjectProperty<ObservableList<StatementTableItem>> existingPredicates;

    private ObservableList<Property> availablePredicates;
    private ObjectProperty<ObservableList<Property>> availPredList;

    private ObjectProperty<Property> selectedPredicate;

    @Inject
    public MetadataDetailsCtrl(ProjectState projectState, MetadataNavState metadataState) {

        this.projectState = projectState;
        this.metadataState = metadataState;

        // notification messages
        notificationMsg = new SimpleStringProperty();

        // existing DataProperties
        statementList = FXCollections.observableList(new ArrayList<>());
        listListener = new ChangeStatementListListener();
        statementList.addListener(listListener);

        existingPredicates = new SimpleObjectProperty<>();

        // adding new DataProperties
        newPredValue = new SimpleStringProperty();
        newPredType = new SimpleObjectProperty<>();
        newPredPromptText = new SimpleStringProperty();
        availablePredicates = FXCollections.observableList(new ArrayList<>());
        availPredList = new SimpleObjectProperty<>();
        selectedPredicate = new SimpleObjectProperty<>();

        // listen on the selected new predicate and set corresponding RDF DataType accordingly
        selectedPredicate.addListener((observable, oldValue, newValue) -> {
            newPredPromptText.set("");
            if (observable != null && newValue != null) {
                RDFDatatype dt = fetchRDFDataType(observable.getValue().getURI());
                newPredType.set(dt);
                newPredPromptText.set("Enter " + (dt == null ? "String" : dt.getJavaClass().getSimpleName()) + " value");
            }
        });

        // listen on changes to the data property list and refresh
        // the list of still available new DataProperties accordingly
        statementList.addListener((ListChangeListener<StatementTableItem>) c -> {
            if (metadataState.selectedNodeProperty().get() != null) {
                Resource currRes = metadataState.selectedNodeProperty().get().getResource();
                availablePredicates.clear();

                OntologyHelper oh = projectState.getMetadata().ontmanager;

                oh.listDatatypeProperties(currRes).stream()
                        .filter(pr -> !pr.isFunctionalProperty() &&
                                !projectState.getMetadata().getAnnotations().contains(currRes, pr))
                        .forEach(curr -> availablePredicates.add(curr.asDatatypeProperty()));

                availPredList.set(availablePredicates);
                newPredValue.set("");
            }
        });

        // listen on changes of the selected parent RDF Resource and update
        // lists of existing and adding new DataProperties accordingly.
        metadataState.selectedNodeProperty().addListener((obs, odlVal, newVal) -> {
            getPage().applyModel(newVal);

            // disable statement listener when statement list is replaced
            // by the selection of a new parent RDF Resource
            listListener.setDisabled();

            availablePredicates.clear();
            if (newVal == null) {
                statementList.clear();
            } else {
                // fetch and set up all existing DataProperties
                statementList.setAll(
                        newVal.getLiterals().stream()
                                .map(StatementTableItem::new)
                                .collect(Collectors.toList())
                );
            }

            listListener.setEnabled();

            // reset notificationMsg label text
            notificationMsg.set("Double click property value to edit the content");
        });
    }

    @Override
    public WebView getWebView() {
        return webView;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        // initialize list of existing DataProperties
        existingPredicates.set(statementList);

        // initialize add new DataProperty components
        availPredList.set(availablePredicates);
        newPredValue.set("");
    }

    // -----------------------------------------
    // Properties
    // -----------------------------------------

    public final StringProperty notificationMsgProperty() { return notificationMsg; }

    public final ObjectProperty<ObservableList<StatementTableItem>> existingPredicatesProperty() { return existingPredicates; }

    public final ObjectProperty<ObservableList<Property>> availablePredicatesProperty() { return availPredList; }
    public final ObjectProperty<Property> selectedPredicateProperty() { return selectedPredicate; }
    public final StringProperty newPredValueProperty() { return newPredValue; }
    public final ObjectProperty<RDFDatatype> newPredTypeProperty() { return newPredType; }
    public final StringProperty newPredPromptTextProperty() { return newPredPromptText; }

    // -----------------------------------------
    // Methods
    // -----------------------------------------

    // Method for creating and adding a new DataProperty to an existing Resource
    public void addDataProperty() {

        if (selectedPredicate.get() != null && !newPredValue.getValue().isEmpty()) {

            // TODO add value consistency checks e.g. entered value is actually required BigInteger etc.
            // requires model.createTypedLiteral(Object) for now all new entries have to be of type double
            RDFDatatype dt = fetchRDFDataType(selectedPredicate.get().getURI());

            if (dt.isValid(newPredValue.getValue())) {

                // fetch parent resource
                Resource parentResource = metadataState.selectedNodeProperty().getValue().getResource();

                StatementTableItem newSTI = new StatementTableItem(
                        getDataPropertyStatement(parentResource,
                                selectedPredicate.get(),
                                newPredValue.getValue(),
                                dt));

                statementList.add(newSTI);

            } else {

                String warningMsg = "Cannot add property! " + newPredValue.getValue()
                        + " is not of required type "+ dt.getJavaClass().getSimpleName() +".";

                notificationMsg.set(warningMsg);
            }
        }

        newPredValue.set("");
    }

    // clear notificationMsg label text when clicked
    public void clearLabel() {
        notificationMsg.set("");
    }

    // TODO part of the logic of this method should probably be move to the metadata service layer
    // TODO create a new DataProperty for an existing resource
    // return the new DataProperty as Statement
    private Statement getDataPropertyStatement(Resource parentResource, Property selProp, String value, RDFDatatype dt) {
        Property p = ResourceFactory.createProperty(selProp.getNameSpace(), selProp.getLocalName());

        RDFNode o = ResourceFactory.createTypedLiteral(value, dt);

        return ResourceFactory.createStatement(parentResource, p, o);
    }

    // TODO replace with actual call to the metadata service layer
    private RDFDatatype fetchRDFDataType(String uri){
        XSDDatatype dt;
        if (uri.equals("http://g-node.org/thomas#hasAuthor")) {
            dt = XSDDatatype.XSDstring;
        } else {
            dt = XSDDatatype.XSDfloat;
        }
        return dt;
    }

}
