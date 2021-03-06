// Copyright (c) 2014, German Neuroinformatics Node (G-Node)
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted under the terms of the BSD License. See
// LICENSE file in the root of the Project.

package gndata.app.ui.util;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.rdf.model.*;

/**
 * Class that implements rendering of table items with RDF literals.
 */
public final class StatementTableItem {

    private final Statement statement;

    public StatementTableItem(Statement statement) {
        this.statement = statement;
    }

    public Statement getStatement() { return statement; }

    public String getPredicate() {
        return statement.getPredicate().getLocalName();
    }

    public String getLiteral() {
        RDFNode object = statement.getObject();

        return object.isLiteral() ? object.asLiteral().getValue().toString() : "";
    }

    public StatementTableItem withLiteral(String val) {

        Resource subj = statement.getSubject();
        Property pred = statement.getPredicate();
        RDFNode obj = ResourceFactory.createPlainLiteral(val);

        return new StatementTableItem(ResourceFactory.createStatement(subj, pred, obj));
    }

    public StatementTableItem withLiteral(String val, RDFDatatype type) {

        Resource subj = statement.getSubject();
        Property pred = statement.getPredicate();
        RDFNode obj = ResourceFactory.createTypedLiteral(val, type);

        return new StatementTableItem(ResourceFactory.createStatement(subj, pred, obj));
    }

    public String getType() {
        RDFNode object = statement.getObject();
        if (object.isLiteral()) {
            RDFDatatype dt = object.asLiteral().getDatatype();
            return dt != null ? dt.getJavaClass().getSimpleName() : "String";
        } else {
            return "";
        }
    }

}
