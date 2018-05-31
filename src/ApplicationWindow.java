
//Imports are listed in full to show what's being used
//could just import javafx.*
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aerofx.AeroFX;

import com.aquafx_project.AquaFx;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mslinks.ShellLink;


public class ApplicationWindow extends Application {


	private static TextField websiteURL;
	private static TextField filePath;

	private static File fileChosen;

	private static FlowPane websiteListPane;
	private static FlowPane fileListPane;

	private static FlowPane websiteURLPane;
	private static FlowPane filePathPane;

	private static ArrayList<Website> scriptSites;
	private static ArrayList<App> scriptFiles;

	private static ListView<String> websiteLabelsListView;
	private static ListView<String> fileLabelsListView;

	private static TextField scriptName;
	
	final static Button removeWebsiteButton = new Button("Remove Selected");
	final static Button updateWebsiteLVButton = new Button("Update Selected");
	final static Button addWebsiteButton = new Button("Add website!");
	final static Button updateWebsiteURLButton = new Button("Update");
	final static Button clearWebsiteButton = new Button("Clear All Websites");
	final static Button submitButton = new Button("Create Shortcut!");

	final static Button browseFilesButton = new Button("Browse files...");
	final static Button addFileButton = new Button("Add File!");
	final static Button removeFileButton = new Button("Remove Selected");
	final static Button updateFileLVButton = new Button("Update Selected");
	final static Button clearFileButton = new Button("Clear All Files");
	final static Button updateFilePathButton = new Button("Update!");

	private final static Label websiteLabelWarning = new Label("Website URLs must begin with https:// or http:// or ftp:// or file:// or www. to be valid");
	private final static Label nonExecutable = new Label("Please select a program executable!");
	private final static String OS = System.getProperty("os.name").toLowerCase();

	public static void main(String[] args) {
		launch(args);
	}
	//starting point for the application
	//this is where we put the code for the user interface
	@Override
	public void start(Stage primaryStage) throws IOException {
		scriptSites = new ArrayList<Website>();
		scriptFiles = new ArrayList<App>();

		websiteListPane = new FlowPane();
		fileListPane = new FlowPane();

		websiteURL = new TextField();

		filePath = new TextField();
		filePath.setEditable(false);

		websiteLabelsListView = new ListView<String>();
		fileLabelsListView = new ListView<String>();


		websiteLabelsListView.setPrefSize(250, 150);
		websiteLabelsListView.setEditable(false);
		websiteLabelsListView.setStyle("-fx-font-weight: bold");

		fileLabelsListView.setPrefSize(250, 150);
		fileLabelsListView.setEditable(false);
		fileLabelsListView.setStyle("-fx-font-weight: bold");


		websiteListPane.getChildren().add(websiteLabelsListView);
		websiteListPane.setPadding(new Insets(20, 20, 20, 0));

		fileListPane.getChildren().add(fileLabelsListView);
		fileListPane.setPadding(new Insets(20, 20, 20, 0));

		//The primaryStage is the top-level container
		primaryStage.setTitle("Easy-Script-Generator");
		BorderPane componentLayout = new BorderPane();
		componentLayout.setPadding(new Insets(20,0,20,20));


		websiteURLPane = new FlowPane();
		websiteURLPane.setHgap(10);
		Label urlLabel = new Label("URL");

		filePathPane = new FlowPane();
		filePathPane.setHgap(10);
		Label pathLabel = new Label("File Location");

		websiteURLPane.getChildren().add(urlLabel);
		websiteURLPane.getChildren().add(websiteURL);

		filePathPane.getChildren().add(pathLabel);

		filePathPane.getChildren().add(filePath);
		
		browseFilesButton.setOnAction(event -> {
			FileChooser chooser = new FileChooser();
			File file = chooser.showOpenDialog(primaryStage);
			if (file != null) {
				nonExecutable.setManaged(false);
				nonExecutable.setVisible(false);
				filePath.setText(file.getName());
				fileChosen = file;
				addFileButton.setManaged(true);
				addFileButton.setVisible(true);
			} else {
				nonExecutable.setManaged(true);
				nonExecutable.setVisible(true);
			}
		});

		addFileButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				scriptFiles.add(new App(fileChosen.getAbsolutePath(), fileChosen.getName()));
				fileLabelsListView.getItems().add(fileChosen.getName());
				addFileButton.setManaged(false);
				addFileButton.setVisible(false);
				removeFileButton.setVisible(true);
				updateFileLVButton.setVisible(true);
				clearFileButton.setVisible(true);
				filePath.clear();
			}
		});

		removeFileButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				String selectedItem = fileLabelsListView.getSelectionModel().getSelectedItem();
				if (selectedItem != null) {
					int selectedIndex = fileLabelsListView.getSelectionModel().getSelectedIndex();
					scriptFiles.remove(selectedIndex);
					fileLabelsListView.getItems().remove(selectedIndex);
				}
				if (fileLabelsListView.getItems().isEmpty()) {
					removeFileButton.setVisible(false);
					updateFileLVButton.setVisible(false);
				}
			}
		});
		
		clearWebsiteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				clearWebsiteInfo();
			}
		});
		
		clearFileButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				clearFileInfo();
			}
		});
		
		updateFileLVButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				String fileToUpdateName = fileLabelsListView.getSelectionModel().getSelectedItem();
				if (fileToUpdateName != null) {
					addFileButton.setVisible(false);
					addFileButton.setManaged(false);
					updateFilePathButton.setVisible(true);
					updateFilePathButton.setManaged(true);
					int selectedIndex = fileLabelsListView.getSelectionModel().getSelectedIndex();
					
					FileChooser chooser = new FileChooser();
					File file = chooser.showOpenDialog(primaryStage);
					if (file != null) {
						nonExecutable.setManaged(false);
						nonExecutable.setVisible(false);
						filePath.setText(file.getName());
						fileChosen = file;
					} else {
						nonExecutable.setManaged(true);
						nonExecutable.setVisible(true);
					}
					updateFilePathButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							App tempApp = new App(fileChosen.getAbsolutePath(), fileChosen.getName());
							filePath.clear();
							fileLabelsListView.getItems().remove(selectedIndex);
							scriptFiles.remove(selectedIndex);
							scriptFiles.add(selectedIndex, tempApp);
							fileLabelsListView.getItems().add(selectedIndex, tempApp.getLabel());
							fileLabelsListView.getSelectionModel().select(selectedIndex);

							fileChosen = null;
							updateFilePathButton.setVisible(false);
							updateFilePathButton.setManaged(false);
						}
					});
				}
			}
		});




		filePathPane.getChildren().add(browseFilesButton);

		addFileButton.setManaged(false);
		addFileButton.setVisible(false);
		filePathPane.getChildren().add(addFileButton);


		removeWebsiteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				String websiteName = websiteLabelsListView.getSelectionModel().getSelectedItem();
				int selectedIndex = websiteLabelsListView.getSelectionModel().getSelectedIndex();
				if (websiteName != null) {
					scriptSites.remove(selectedIndex);
					websiteLabelsListView.getItems().remove(selectedIndex);
				}
				if (websiteLabelsListView.getItems().isEmpty()) {
					removeWebsiteButton.setVisible(false);
					updateWebsiteLVButton.setVisible(false);
				}
			}
		});


		addWebsiteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String strURL = websiteURL.getText();
				String regex = "^(((https?|ftp|file)://)|(www.))[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(strURL);	
				if (strURL != null && m.find()) {
					websiteLabelWarning.setManaged(false);
					websiteLabelWarning.setVisible(false);
					removeWebsiteButton.setVisible(true);
					updateWebsiteLVButton.setVisible(true);
					clearWebsiteButton.setVisible(true);
					String websiteName = websiteURL.getText().replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)","");
					Website website = new Website(websiteURL.getText(), websiteName);
					scriptSites.add(website);
					websiteURL.clear();
					websiteLabelsListView.getItems().addAll(website.getLabel());
				}
				else {
					websiteLabelWarning.setManaged(true);
					websiteLabelWarning.setVisible(true);
				}
			}
		});

		websiteURLPane.getChildren().add(addWebsiteButton);

		updateWebsiteLVButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				String websiteToUpdateName = websiteLabelsListView.getSelectionModel().getSelectedItem();
				if (websiteToUpdateName != null) {
					addWebsiteButton.setVisible(false);
					addWebsiteButton.setManaged(false);
					updateWebsiteURLButton.setVisible(true);
					updateWebsiteURLButton.setManaged(true);
					int selectedIndex = websiteLabelsListView.getSelectionModel().getSelectedIndex();
					websiteURL.setText(scriptSites.get(selectedIndex).getURL());

					updateWebsiteURLButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							String websiteToAddName = websiteURL.getText().replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)","");
							Website tempWebsite = new Website(websiteURL.getText(), websiteToAddName);
							websiteURL.clear();
							websiteLabelsListView.getItems().remove(selectedIndex);
							scriptSites.remove(selectedIndex);
							scriptSites.add(selectedIndex, tempWebsite);
							websiteLabelsListView.getItems().add(selectedIndex, tempWebsite.getLabel());
							websiteLabelsListView.getSelectionModel().select(selectedIndex);


							updateWebsiteURLButton.setVisible(false);
							updateWebsiteURLButton.setManaged(false);
							addWebsiteButton.setManaged(true);
							addWebsiteButton.setVisible(true);
						}
					});
				}
			}
		});

		updateWebsiteURLButton.setVisible(false);
		updateWebsiteURLButton.setManaged(false);
		websiteURLPane.getChildren().add(updateWebsiteURLButton);
		
		updateFilePathButton.setVisible(false);
		updateFilePathButton.setManaged(false);
		filePathPane.getChildren().add(updateFilePathButton);

		websiteLabelWarning.setTextFill(Color.RED);
		websiteLabelWarning.setManaged(false);
		websiteLabelWarning.setVisible(false);
		websiteURLPane.getChildren().add(websiteLabelWarning);

		nonExecutable.setTextFill(Color.RED);
		nonExecutable.setManaged(false);
		nonExecutable.setVisible(false);
		filePathPane.getChildren().add(nonExecutable);

		removeWebsiteButton.setVisible(false);
		updateWebsiteLVButton.setVisible(false);
		clearWebsiteButton.setVisible(false);
		
		removeFileButton.setVisible(false);
		updateFileLVButton.setVisible(false);
		clearFileButton.setVisible(false);
		
		BorderPane websiteOptionsPane = new BorderPane();
		websiteOptionsPane.setTop(removeWebsiteButton);
		websiteOptionsPane.setCenter(clearWebsiteButton);
		websiteOptionsPane.setBottom(updateWebsiteLVButton);
		websiteListPane.getChildren().add(websiteOptionsPane);
		
		BorderPane fileOptionsPane = new BorderPane();
		fileOptionsPane.setTop(removeFileButton);
		fileOptionsPane.setCenter(clearFileButton);
		fileOptionsPane.setBottom(updateFileLVButton);
		fileListPane.getChildren().add(fileOptionsPane);


		websiteURLPane.getChildren().add(websiteListPane);

		filePathPane.getChildren().add(fileListPane);


















		componentLayout.setTop(websiteURLPane);
		componentLayout.setCenter(filePathPane);

		submitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (scriptSites.isEmpty() == false) {
					final Stage dialog = new Stage();
					dialog.initModality(Modality.APPLICATION_MODAL);
					dialog.initOwner(primaryStage);
					BorderPane popupPane = new BorderPane();
					popupPane.getChildren().add(new Text("Script Generate Confirmation"));
					Scene dialogScene = new Scene(popupPane, 300, 200);
					dialog.setScene(dialogScene);
					dialog.show();

					scriptName = new TextField();
					Label scriptNameLabel = new Label("Pick a name for your generated script!");

					FlowPane scriptPane = new FlowPane();
					scriptPane.getChildren().addAll(scriptNameLabel);
					scriptPane.getChildren().addAll(scriptName);
					popupPane.setCenter(scriptPane);

					Button popupOKBtn = new Button("OK");

					FlowPane bottomPane = new FlowPane();

					popupOKBtn.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							if (scriptName != null) {

								try {
									createBatch();
									clearWebsiteInfo();
									clearFileInfo();
									dialog.hide();

								} catch (FileNotFoundException e) {
									Label label = new Label("File already exists, please select a different name.");
									label.setTextFill(Color.RED);
									bottomPane.getChildren().add(label);
									popupPane.setBottom(bottomPane);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					});

					bottomPane.getChildren().add(popupOKBtn);
					popupPane.setBottom(bottomPane);
				}
			}
		});

		websiteLabelsListView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				updateWebsiteURLButton.setVisible(false);
				updateWebsiteURLButton.setManaged(false);
				addWebsiteButton.setVisible(true);
				addWebsiteButton.setManaged(true);
				websiteURL.setText(null);
			}
		});


		FlowPane bottomPane = new FlowPane();
		bottomPane.getChildren().add(submitButton);
		componentLayout.setBottom(bottomPane);


		Scene appScene = new Scene(componentLayout,500,500);
		
		if (isMac()) {
			AquaFx.style();
		} else if (isWindows()) {
			AeroFX.style();
		}
		
		primaryStage.setScene(appScene);
		primaryStage.show();

		primaryStage.setResizable(false);
		
	}
	
	public static void clearWebsiteInfo() {
		websiteLabelsListView.getItems().clear();
		scriptSites.clear();
		removeWebsiteButton.setVisible(false);
		updateWebsiteLVButton.setVisible(false);
		clearWebsiteButton.setVisible(false);
		websiteURL.clear();
	}
	
	public static void clearFileInfo() {
		fileLabelsListView.getItems().clear();
		scriptFiles.clear();
		removeFileButton.setVisible(false);
		updateFileLVButton.setVisible(false);
		updateFilePathButton.setVisible(false);
		addFileButton.setVisible(false);
		clearFileButton.setVisible(false);
		filePath.clear();
	}

	public static boolean isWindows() {

		return (OS.indexOf("win") >= 0);

	}

	public static boolean isMac() {

		return (OS.indexOf("mac") >= 0);

	}

	public static void createBatch() throws IOException, FileNotFoundException { 

		if (isWindows()) {
			File file=new File("C:\\Users\\Public\\Desktop\\" + scriptName.getText() + ".bat"); 
			file.setReadable(false,false);
			FileOutputStream fos=new FileOutputStream(file); 
			DataOutputStream dos=new DataOutputStream(fos); 
			String newLine = System.getProperty("line.separator");
			for (Website website: scriptSites) {
				if (scriptSites.get(0) == website) {
					dos.writeBytes("call start /w chrome.exe -new-window " + website.getURL()); 
					dos.writeBytes(newLine);
				}
				else {
					dos.writeBytes("call start /w chrome.exe " + website.getURL()); 
					dos.writeBytes(newLine);
				}
				file.setReadOnly();
			}
			
			for (App currApp: scriptFiles) {
				dos.writeBytes("start " + currApp.getPath() + " " + currApp.getLabel()); 
				dos.writeBytes(newLine);
			}
			dos.close();
			fos.close();
			ShellLink.createLink("C:\\Users\\Public\\Desktop\\" + scriptName.getText() + ".bat", "C:\\Users\\Public\\Desktop\\" + scriptName.getText() + ".lnk");
		} else if (isMac()) {
			File file = new File(System.getProperty("user.home") + "/Desktop/" + scriptName.getText() + ".command"); 
			FileOutputStream fos=new FileOutputStream(file); 
			DataOutputStream dos=new DataOutputStream(fos); 
			String newLine = System.getProperty("line.separator");
			dos.writeBytes("#!/bin/bash");
			dos.writeBytes(newLine);
			for (Website website: scriptSites) {
				if (scriptSites.get(0) == website) {
					dos.writeBytes("open -na 'Google Chrome' --args --new-window " + website.getURL()); 
					dos.writeBytes(newLine);
					file.setReadOnly();
					dos.writeBytes("wait");
					dos.writeBytes(newLine);
				}
				else if (scriptSites.get(scriptSites.size() - 1) == website) {
					dos.writeBytes("open -na 'Google Chrome' " + website.getURL()); 
					dos.writeBytes(newLine);
				}
				else {
					dos.writeBytes("open -na 'Google Chrome' " + website.getURL()); 
					dos.writeBytes(newLine);
				}
			}
			for (App currApp: scriptFiles) {
				dos.writeBytes("open '" + currApp.getPath() + "'"); 
				dos.writeBytes(newLine);
			}
			dos.close();
			fos.close();
			file.setExecutable(true);
		} else {
			System.out.println("Your OS is not supported!!");
		}
	}
}