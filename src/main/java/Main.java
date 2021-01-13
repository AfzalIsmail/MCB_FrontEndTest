import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Main class
 */
public class Main extends Application {

    private Stage mainWindow;

    /**
     * Main function
     * @param args
     */
    public static void main(String[] args) {

        launch(args);

    }

    /**
     * Function to start and show the UI
     * @param primaryStage
     * @throws IOException
     */
    public void start(Stage primaryStage) throws IOException{

        mainWindow = primaryStage;

        //Main layout
        BorderPane borderPane = new BorderPane();

        //Tabs for searching/getting and posting JSON
        TabPane tabPane = new TabPane();

        Tab search = new Tab("Search");
        search.setClosable(false);
        VBox searchContent = searchTabContent(mainWindow);
        searchContent.setPadding(new Insets(10,10,10,10));
        search.setContent(searchContent);

        Tab post = new Tab("Post");
        post.setClosable(false);
        VBox postContent = postTabContent();
        postContent.setPadding(new Insets(10,10,10,10));
        post.setContent(postContent);

        tabPane.getTabs().addAll(search, post);

        borderPane.setCenter(tabPane);

        Scene scene = new Scene(borderPane, 500, 750);

        mainWindow.setScene(scene);

        mainWindow.show();

    }

    /**
     * Method to return a VBox that returns the contents for the search tab.
     * @param stage to open the file dialog to export posts to CSV
     * @return VBox content for the search tab
     */
    private VBox searchTabContent(Stage stage){

        VBox content = new VBox(10);
        //content.setMaxSize(500,750);

        //---------------------------------------------------------------search by post label
        Label searchLine = new Label("Search 'posts' by: ");

        //---------------------------------------------------------------radio buttons to specify search
        RadioButton id = new RadioButton("ID");
        RadioButton userId = new RadioButton("userID");
        RadioButton title = new RadioButton("title");
        //RadioButton body = new RadioButton("body");

        ToggleGroup searchBy = new ToggleGroup();
        id.setToggleGroup(searchBy);
        userId.setToggleGroup(searchBy);
        title.setToggleGroup(searchBy);
        //body.setToggleGroup(searchBy);

        HBox selection = new HBox(10);
        selection.getChildren().addAll(id, userId, title);

        //---------------------------------------------------------------text filed to receive user input
        TextField searchPosts = new TextField();

        //---------------------------------------------------------------checkbox to filter query by sorting title in ascending order
        CheckBox sortTitle = new CheckBox("Sort by 'title'");
        //---------------------------------------------------------------checkbox to filter query by searching for specific words in body
        CheckBox bodyFilter = new CheckBox("Filter body with key words:");
        //---------------------------------------------------------------text field to receive user input to filter body
        TextField bodyFilterField = new TextField();
        //---------------------------------------------------------------button to start query
        Button searchPostsButton = new Button("Search Posts");

        //---------------------------------------------------------------search by comments
        Label searchComments = new Label("\nSearch 'comments' by: \n" +
                                        "Post ID: ");

        //---------------------------------------------------------------radio buttons to specify search query
        RadioButton commentPostId = new RadioButton("Post ID");
        RadioButton commentId = new RadioButton("Comment ID");
        RadioButton commentName = new RadioButton("Comment Name");
        RadioButton commentEmail = new RadioButton("Comment Email");

        ToggleGroup searchCommentsBy = new ToggleGroup();
        commentPostId.setToggleGroup(searchCommentsBy);
        commentId.setToggleGroup(searchCommentsBy);
        commentName.setToggleGroup(searchCommentsBy);
        commentEmail.setToggleGroup(searchCommentsBy);

        //---------------------------------------------------------------text field to specify the postId comments to be queried
        TextField postId = new TextField();

        HBox commentSelection = new HBox(10);
        commentSelection.getChildren().addAll(commentPostId, commentId, commentName, commentEmail);

        //---------------------------------------------------------------text field to specify specific words to filter body of JSON objects
        TextField searchCommentsField = new TextField();

        //---------------------------------------------------------------button to start search query
        Button searchCommentsButton = new Button("Search Comments");

        //---------------------------------------------------------------Area to display the results of the query
        Label searchResult = new Label("\nResluts:");
        VBox result = new VBox();
        result.setMaxHeight(180);
        ScrollPane resultsPane = new ScrollPane();
        resultsPane.setPadding(new Insets(5,5,5,5));

        Text results = new Text();
        resultsPane.setContent(results);
        resultsPane.setHmax(450);
        result.getChildren().addAll(resultsPane);

        //---------------------------------------------------------------button to export searched posts to CSV
        Button exportData = new Button("Export to CSV");

        //---------------------------------------------------------------Button event for when the searchPost button is pressed
        searchPostsButton.setOnAction(e -> {

            String search = searchPosts.getText();
            String searchTitle = search.replace(" ", "%20");


            URL urlGet = null;

            //---------------------------------------------------------------setting the URL depending on which radio button is selected to query the JSON server
            if(id.isSelected()){

                try {

                    urlGet = new URL("https://jsonplaceholder.typicode.com/posts?id="+search);

                }catch (Exception e1){

                    System.out.println(e1.getMessage());
                }

            }if(userId.isSelected()){

                try {

                    urlGet = new URL("https://jsonplaceholder.typicode.com/posts?userId="+search);

                }catch (Exception e1){System.out.println(e1.getMessage());}

            }if(title.isSelected()){

                try {

                    urlGet = new URL("https://jsonplaceholder.typicode.com/posts?title="+searchTitle);

                }catch (Exception e1){System.out.println(e1.getMessage());}

            }

            try {

                String line = null;

                //---------------------------------------------------------------setting up connection
                HttpURLConnection connection = (HttpURLConnection) urlGet.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuffer response = new StringBuffer();

                    while ((line = in.readLine()) != null) {

                        response.append(line + "\n");

                    }

                    in.close();

                    String responseString = response.toString();

                    //---------------------------------------------------------------Converting the JSON string to a Java object using the GSON Maven repository
                    Gson gson = new Gson();
                    Type postsListType = new TypeToken<ArrayList<Posts>>(){}.getType();
                    ArrayList<Posts> posts = gson.fromJson(responseString, postsListType);

                    if(bodyFilter.isSelected()){

                        ArrayList<Posts> temp = new ArrayList<>();

                        for(Posts p:posts){

                            if(p.getBody().contains(bodyFilterField.getText())){

                                System.out.println(p.toString());
                                temp.add(p);

                            }

                        }

                        if(sortTitle.isSelected()){

                            Collections.sort(temp);
                            results.setText(temp.toString());

                            exportData.setOnAction(e1 -> {

                                try {
                                    export(temp, stage);
                                }catch (IOException e2){System.out.println(e2.getMessage());}

                            });

                        }else{

                            results.setText(temp.toString());

                            exportData.setOnAction(e1 -> {

                                try {
                                    export(temp, stage);
                                }catch (IOException e2){System.out.println(e2.getMessage());}

                            });

                        }

                    }else{

                        if(sortTitle.isSelected()){

                            Collections.sort(posts);
                            results.setText(posts.toString());

                            exportData.setOnAction(e1 -> {

                                try {
                                    export(posts, stage);
                                }catch (IOException e2){}

                            });

                        }else{

                            results.setText(posts.toString());
                            exportData.setOnAction(e1 -> {

                                try {
                                    export(posts, stage);
                                }catch (IOException e2){System.out.println(e2.getMessage());}

                            });

                        }

                    }

                } else {

                    results.setText("Connection unsuccessful. \n" +
                            "Response code: " + connection.getResponseCode() + "\n" +
                            "Response message: " + connection.getResponseMessage());

                    System.out.println("Not worked");

                }

            }catch (Exception e1){


            }

        });

        searchCommentsButton.setOnAction(e ->{

            String search = searchCommentsField.getText();
            String searchTitle = search.replace(" ", "%20");

            int postIdVal = Integer.parseInt(postId.getText());

            URL urlGet = null;

            if(commentPostId.isSelected()){

                try {

                    urlGet = new URL("https://jsonplaceholder.typicode.com/posts/" + postIdVal + "/comments?postId=" + search);

                }catch (Exception e1){System.out.println(e1.getMessage());}

            }if(commentId.isSelected()){

                try {

                    urlGet = new URL("https://jsonplaceholder.typicode.com/posts/" + postIdVal + "/comments?id=" + search);

                }catch (Exception e1){System.out.println(e1.getMessage());}

            }if(commentName.isSelected()){

                try {

                    urlGet = new URL("https://jsonplaceholder.typicode.com/posts/" + postIdVal + "/comments?name=" + searchTitle);

                }catch (Exception e1){System.out.println(e1.getMessage());}

            }if(commentEmail.isSelected()){

                try {

                    urlGet = new URL("https://jsonplaceholder.typicode.com/posts/" + postIdVal + "/comments?email=" + search);

                }catch (Exception e1){System.out.println(e1.getMessage());}

            }

            try {

                String line = null;

                HttpURLConnection connection = (HttpURLConnection) urlGet.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuffer response = new StringBuffer();

                    while ((line = in.readLine()) != null) {

                        response.append(line + "\n");

                    }

                    in.close();

                    System.out.println("Result: " + response.toString());
                    results.setText(response.toString());


                } else {

                    System.out.println("Not worked");

                }

            }catch (Exception e1){

                System.out.println(e1.getMessage());

            }

        });

        content.getChildren().addAll(searchLine, selection, searchPosts, sortTitle, bodyFilter, bodyFilterField, searchPostsButton, searchComments, postId, commentSelection, searchCommentsField, searchCommentsButton, searchResult, result, exportData);

        return content;

    }

    /**
     * Method to return the contents of the post tab
     * @return VBox with content for the post tab
     * @throws IOException
     */
    private VBox postTabContent() throws IOException {

        VBox content = new VBox();

        //---------------------------------------------------------------specifying JSON server
        URL urlPost = new URL("https://jsonplaceholder.typicode.com/posts/" );

        //---------------------------------------------------------------Input fields for the user to create and send a new JSON object
        Label userId = new Label("User ID");
        TextField userIdField = new TextField();
        //int userIdVal = Integer.parseInt(userIdField.getText());

        Label id = new Label("ID");
        TextField idField = new TextField();
        //int idVal = Integer.parseInt(idField.getText());

        Label title = new Label("Title");
        TextField titleField = new TextField();

        Label body = new Label("Body");
        TextField bodyField = new TextField();

        Button post = new Button("Post");

        //---------------------------------------------------------------Area to display the status of the post
        Label searchResult = new Label("Resluts:");
        VBox result = new VBox();
        ScrollPane resultsPane = new ScrollPane();
        resultsPane.setPadding(new Insets(5,5,5,5));

        Text results = new Text();
        resultsPane.setContent(results);
        resultsPane.setHmax(450);
        result.getChildren().addAll(resultsPane);

        //--------------------------------------------------------------- event to start the post query
        post.setOnAction(e ->{

            //---------------------------------------------------------------validating the user input to prevent a post from having a null 'title' field
            if(titleField.getText().length() == 0){

                results.setText("Please enter a 'title' value.");

            }
            //---------------------------------------------------------------validating that the body of the JSON object is less than 400 characters
            if(bodyField.getText().length() > 400){

                results.setText("The length of the body exceeds 400 characters.");

            }else{

                String postParams = "{\n" + "\"userID\": " + userIdField.getText() + ", \r\n" +
                        "   \"id\": " + idField.getText() + ", \r\n" +
                        "   \"title\": \"" + titleField.getText() + "\", \r\n" +
                        "   \"body\": \"" + bodyField.getText() + "\" \n}";


                //---------------------------------------------------------------Connecting to server
                try {

                    HttpURLConnection postConnection = (HttpURLConnection) urlPost.openConnection();
                    postConnection.setRequestMethod("POST");
                    postConnection.setRequestProperty("Content-Type", "application/json");

                    postConnection.setDoOutput(true);
                    OutputStream outputStream = postConnection.getOutputStream();
                    outputStream.write(postParams.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    int responseCode = postConnection.getResponseCode();
                    System.out.println("Post response code: " + responseCode);
                    System.out.println("Post response message: " + postConnection.getResponseMessage());

                    if (responseCode == HttpURLConnection.HTTP_CREATED) {

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));

                        String input;
                        StringBuffer response = new StringBuffer();

                        while ((input = bufferedReader.readLine()) != null) {

                            response.append(input);


                        }
                        bufferedReader.close();

                        System.out.println(response.toString());

                        //---------------------------------------------------------------Displaying the results of the post
                        results.setText("Post response code: " + responseCode + "\n\n" +
                                "Post response message: " + postConnection.getResponseMessage() + "\n\n" +
                                response.toString());

                    } else {

                        System.out.println("Post did not work");

                    }

                } catch (Exception e1) {

                    System.out.println(e1.getMessage());

                }
            }

        });

        content.getChildren().addAll(userId, userIdField, id, idField, title, titleField, body, bodyField, post, searchResult, result);


        return content;

    }

    /**
     * Function to export the posts displayed on the UI as CSV
     * @param posts Arraylists of Posts objects that were searched and that will be written in the CSV file
     * @param stage the UI stage where the file saving window will be opened
     * @throws IOException
     */
    private static void export(ArrayList<Posts> posts, Stage stage) throws IOException{

        //---------------------------------------------------------------Choose the location to save the file
        FileChooser fileChooser = new FileChooser();

        File file = fileChooser.showSaveDialog(stage);

        String filePath = null;

        if(file != null){

            filePath = file.getPath();

        }

        //---------------------------------------------------------------Writing the headers
        FileWriter fileWriter = new FileWriter(filePath);

        fileWriter.append("UserID");
        fileWriter.append(",");
        fileWriter.append("ID");
        fileWriter.append(",");
        fileWriter.append("title");
        fileWriter.append(",");
        fileWriter.append("body");
        fileWriter.append("\n");

        //---------------------------------------------------------------writing the instances for each Posts object and separating them with ","
        for(Posts p:posts){

            fileWriter.append(Integer.toString(p.getUserId()));
            fileWriter.append(",");
            fileWriter.append(Integer.toString(p.getId()));
            fileWriter.append(",");
            fileWriter.append(p.getTitle());
            fileWriter.append(",");
            fileWriter.append(p.getBody());
            fileWriter.append("\n");

        }

        fileWriter.flush();
        fileWriter.close();

    }

}
