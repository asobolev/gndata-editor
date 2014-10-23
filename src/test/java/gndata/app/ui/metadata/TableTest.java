package gndata.app.ui.metadata;

import java.util.Optional;
import javafx.collections.ObservableList;

import com.hp.hpl.jena.rdf.model.*;
import gndata.app.ui.metadata.table.*;
import gndata.lib.util.FakeRDFModel;
import org.junit.Test;


public class TableTest {

    private final Model model = FakeRDFModel.getFakeAnnotations();

    @Test
    public void testTableCtrl() throws Exception {
        Resource tbl = model.getResource(FakeRDFModel.tbl);
        ObservableList<TableItem> l = TableCtrl.buildTableItems(tbl);

        // list should only contain literals, one 'name' item in this case
        Optional<TableItem> t = l.stream().filter(a -> a.getPredicate().equals("name")).findFirst();
        assert(t.isPresent());
        assert(t.get().getLiteral().equals("Tim Berners-Lee"));
    }

    @Test
    public void testTableItem() throws Exception {
        Resource tbl = model.getResource(FakeRDFModel.tbl);
        Property p = model.getProperty(model.getNsPrefixURI("foaf"), "name");
        Statement st = tbl.getProperty(p);

        TableItem t = new TableItem(st.getPredicate(), st.getLiteral());

        assert(t.getPredicate().equals("name"));
        assert(t.getLiteral().equals("Tim Berners-Lee"));
    }
}
