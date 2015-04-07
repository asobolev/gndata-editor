package gndata.lib.srv.orm;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Created by andrey on 07.04.15.
 */
public class EntityType {

    private Resource res;

    public EntityType(Resource res) {
        // TODO validate res is an existing OWL type

        this.res = res;
    }

    public String getNameSpace() {
        return res.getNameSpace();
    }

    public String getLocalName() {
        return res.getLocalName();
    }

    public String getLabel() {
        // TODO

        return res.getLocalName();
    }
}
