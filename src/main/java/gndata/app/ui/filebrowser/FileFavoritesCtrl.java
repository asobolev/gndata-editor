package gndata.app.ui.filebrowser;

import java.net.URL;
import java.nio.file.*;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.control.*;

import com.google.inject.Inject;
import gndata.app.state.*;
import gndata.lib.srv.*;

/**
 * Controller for {@link FileFavoritesView}
 */
public class FileFavoritesCtrl implements Initializable {

    @FXML
    private ListView<FileAdapter> fileFavorites;
    @FXML
    private Button openFileFavoriteHandling;

    private final FileNavigationState navState;

    @Inject
    public FileFavoritesCtrl(ProjectState projectState, FileNavigationState navState){

        this.navState = navState;

        projectState.configProperty().addListener((p, o, n) -> {
            if (n == null)
                return;

            Path path = Paths.get(n.getProjectPath());
            this.navState.getFavoriteFolders().add(new LocalFile(path));
            this.navState.getFavoriteFolders().add(new LocalFile(path.resolve(Paths.get("schemas"))));
        });

        // unselected file favorite, if the selected file favorite is not the same as the
        // current selected parent of the navigation state any longer
        this.navState.selectedParentProperty().addListener((p, o, n) -> {
            if (n == null || n.equals(fileFavorites.getSelectionModel().getSelectedItem()))
                return;

            fileFavorites.getSelectionModel().clearSelection();
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        fileFavorites.setItems(navState.getFavoriteFolders());

        fileFavorites.getSelectionModel().selectedItemProperty().addListener((p, o, n) -> {
            if (n == null)
                return;
            navState.setSelectedParent(n);
        });
    }
}
