
//Imports are listed in full to show what's being used
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * This class is the main file for the EZScripts Application.
 * Copyright: Do not distribute this application without owner's consent, "Matt Vieten"
 * Email: matt.vieten12@gmail.com
 * @author mvieten
 *
 */
public class ApplicationWindow extends Application {

	private ArrayList<ApplicationPage> appPages;
	protected static TabPane tabPane;

	private Scene appScene;

	/**
	 * Main method that launches the gui application.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
	//starting point for the application
	//this is where we put the code for the user interface

	/**
	 * This method creates the GUI application and initializes all of its' properties.
	 * @param stage - Main stage for application
	 */
	@Override
	public void start(Stage stage) throws IOException {

		/**
		 * Sets title of application to "EZScripts"
		 */
		stage.setTitle("EZScripts");

		/**
		 * Creates tab pane and tabs at the top of application for creating new scripts and updating old scripts.
		 */
		tabPane = new TabPane();

		tabPane = new TabPane();
		final Tab newTab = new Tab("+");
		newTab.setClosable(false);
		tabPane.getTabs().add(newTab);
		createAndSelectNewTab(tabPane, "Untitled");

		appPages = new ArrayList<ApplicationPage>();
		appPages.add(new ApplicationPage(new Script(new ArrayList<Website>(), new ArrayList<App>())));
		appPages.get(appPages.size() - 1).appLayout.setTop(tabPane);

		tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> observable,
					Tab oldSelectedTab, Tab newSelectedTab) {
				int index = tabPane.getTabs().indexOf(newSelectedTab);
				if (newSelectedTab == newTab) {
					appPages.add(new ApplicationPage(new Script(new ArrayList<Website>(), new ArrayList<App>())));
					index = tabPane.getTabs().indexOf(newSelectedTab);
					createAndSelectNewTab(tabPane, "Untitled " + (tabPane.getTabs().size()));
				}
				appScene = appPages.get(index).appScene;
				appPages.get(index).appLayout.setTop(tabPane);
				stage.setScene(appScene);
				stage.show();
			}
		});

		appPages.get(appPages.size() - 1).appLayout.setTop(tabPane);
		appScene = appPages.get(appPages.size() - 1).appScene;

		stage.setScene(appScene);
		stage.show();
		stage.setResizable(false);
	}

	private Tab createAndSelectNewTab(final TabPane tabPane, final String title) {
		Tab tab = new Tab(title);
		final ObservableList<Tab> tabs = tabPane.getTabs();
		tab.closableProperty().bind(Bindings.size(tabs).greaterThan(2));
		tab.setOnCloseRequest(e -> {
			int index = tabPane.getTabs().indexOf(tab);
			appPages.remove(index);
		});
		tabs.add(tabs.size() - 1, tab);
		tabPane.getSelectionModel().select(tab);
		return tab;
	}
}