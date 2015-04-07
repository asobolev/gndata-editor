package gndata.lib.srv.orm;

import java.util.*;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;

/**
 * Created by andrey on 07.04.15.
 */
public class QuerySet {

    private Model model;
    private OntModel ontology;
    private EntityType eType;
    private List<MetadataEntity> selected;

    public QuerySet(Model model, EntityType eType, List<MetadataEntity> selected) {
        this.model = model;
        this.eType = eType;
        this.selected = selected;
    }

    public List<MetadataEntity> toList() {
        return selected;
    }

    public QuerySet all() {
        return new QuerySet(model, eType, selected);
    }

    public QuerySet filter(Map<String, String> filt) {
        // TODO

        return new QuerySet(model, eType, selected);
    }

    public QuerySet filter(EntityType relationType, Map<String, String> filt) {
        // TODO

        return new QuerySet(model, eType, selected);
    }

    public QuerySet exclude(Map<String, String> filt) {
        // TODO

        return new QuerySet(model, eType, selected);
    }

    public QuerySet exclude(EntityType relationType, Map<String, String> filt) {
        // TODO

        return new QuerySet(model, eType, selected);
    }

    public Boolean delete() {
        // TODO

        return true;
    }

    public MetadataEntity create(Map<Property, Literal> attrs) {
        Resource res = model.createResource();

        // TODO assign properties in line with OWL

        return new MetadataEntity(ontology, res);
    }
}
