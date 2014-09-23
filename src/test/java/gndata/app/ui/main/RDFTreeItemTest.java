package gndata.app.ui.main;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import gndata.app.ui.util.RDFTreeItem;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.apache.jena.riot.RDFDataMgr;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.net.URL;
import java.util.Optional;


public class RDFTreeItemTest {

    private Model model;
    private Resource person;
    private RDFTreeItem root;

    private static String tbl = "http://www.w3.org/People/Berners-Lee/card#i";
    private static String rhm = "http://dig.csail.mit.edu/2007/wiki/people/RobertHoffmann#RMH";

    /**
     * Returns a child TreeItem within the children of a given TreeItem.
     *
     * @param where     TreeItem node where to search
     * @param name      String of the child resource to search
     * @return          Optional TreeItem
     */
    private static Optional<TreeItem<Resource>> getChild(TreeItem<Resource> where, String name) {
        return where.getChildren()
                .stream()
                .filter(a -> a
                        .getValue()
                        .toString()
                        .equals(name))
                .findFirst();
    }

    @Before
    public void setUp() throws Exception {
        URL name = getClass().getResource("/resources/foaf.rdf");
        model = RDFDataMgr.loadModel(name.toString());

        person = model.getResource("http://xmlns.com/foaf/0.1/Person");
        root = new RDFTreeItem(model, person);
    }

    @Test
    public void testHasNodes() throws Exception {
        assertFalse(root.isLeaf());
    }

    @Test
    public void testParent() throws Exception {
        assertEquals(getChild(root, tbl).get().getParent(), root);
    }

    @Test
    public void testResource() throws Exception {
        // ensure Tim is in the list of persons
        TreeItem<Resource> tbl_node = getChild(root, tbl).get();

        // ensure Tim knows Robert
        TreeItem<Resource> rhm_node = getChild(tbl_node, rhm).get();

        // ensure Robert "parent" friend is Tim
        assertEquals(rhm_node.getParent(), tbl_node);

        // ensure Tim is not in the Robert's "children" friends list
        assertFalse(getChild(rhm_node, tbl).isPresent());
    }
}