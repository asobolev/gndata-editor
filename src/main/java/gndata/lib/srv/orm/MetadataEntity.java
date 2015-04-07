package gndata.lib.srv.orm;

import java.util.*;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;

/**
 * Created by andrey on 07.04.15.
 */
public class MetadataEntity {

    private Model model;
    private OntModel ontology;
    private Resource res;

    public MetadataEntity(OntModel ontology, Resource res) {
        this.ontology = ontology;
        this.model = res.getModel();
        this.res = res;
    }

    public DataPropertiesList getProperties() {
        Map<Property, Literal> fake = new HashMap<>();

        return new DataPropertiesList(fake);
    }

    public ObjectPropertiesList getRelations() {
        Map<EntityType, QuerySet> fake = new HashMap<>();

        return new ObjectPropertiesList(fake);
    }

    /*

    Helper classes to manage Data Properties / Object Properties

    */

    private class DataPropertiesList extends HashMap<Property, Literal> {

        private Resource res;

        public DataPropertiesList(Map<Property, Literal> props) {
            super();

            this.putAll(props);
        }

        // TODO implement all modification methods
        // TODO like put, remove, setValue etc.
    }

    private class ObjectPropertiesList extends HashMap<EntityType, QuerySet> {

        private Resource res;

        public ObjectPropertiesList(Map<EntityType, QuerySet> related) {
            super();

            this.putAll(related);
        }

        // TODO implement all modification methods
        // TODO like put, remove, setValue etc.
    }
}
