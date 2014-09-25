package gndata.app.ui.main;

import gndata.app.state.AppState;
import gndata.app.state.ProjectState;
import gndata.app.ui.dia.ProjectConfigView;
import gndata.lib.config.GlobalConfig;
import gndata.lib.config.GlobalConfig.ProjectItem;
import gndata.lib.config.ProjectConfig;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.stage.DirectoryChooser;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Controller for the main menu view.
 */
public class MenuCtrl {

    @FXML private MenuBar menu;

    private final AppState appState;
    private final ProjectState projectState;

    @Inject
    public MenuCtrl(AppState appState, ProjectState projectState) {
        this.appState = appState;
        this.projectState = projectState;
    }

    /**
     * Open a new project or create one.
     */
    public void createProject() {
        File selected = showDirectoryChooser();

        if (selected == null)
            return;

        try {
            ProjectConfig config = ProjectConfig.load(selected.getAbsolutePath());
            config = showConfigDialog(config);

            if (config != null) {
                config.store();
                projectState.setConfig(config);

                GlobalConfig globalConfig = appState.getConfig();
                Optional<ProjectItem> item = globalConfig.getProject(config.getProjectPath());
                if (item.isPresent()) {
                    item.get().name = config.getName();
                } else {
                    globalConfig.appendProject(config.getProjectPath(), config.getName());
                }
                globalConfig.store();
            }
        } catch (IOException e) {
            // TODO nice exception dialog here
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Shows a directory chooser dialog.
     *
     * @return The selected dialog or null if the selection was canceled.
     */
    public File showDirectoryChooser() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select the project directory");
        dirChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        return dirChooser.showDialog(menu.getScene().getWindow());
    }

    /**
     * Shows a project config dialog.
     *
     * @param config    The configuration to edit in the dialog.
     *
     * @return The edited
     */
    public ProjectConfig showConfigDialog(ProjectConfig config) {
        ProjectConfigView configDialog = new ProjectConfigView(config);
        return configDialog.showDialog(menu.getScene().getWindow());
    }

    /**
     * Open a previously opened project.
     */
    public void openProject() {
        System.out.println("openProject");
    }

    /**
     * Set the project state to not running.
     */
    public void exit() {
        appState.setRunning(false);
    }
}
