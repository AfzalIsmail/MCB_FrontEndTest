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

public class Main extends Application {

    private Stage mainWindow;

    public static void main(String[] args) {

        launch(args);

    }

    public void start(Stage primaryStage) throws IOException{

        mainWindow = primaryStage;

        BorderPane borderPane = new BorderPane();

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

    private VBox searchTabContent(Stage stage){

        VBox content = new VBox(10);
        //content.setMaxSize(500,750);

        Label searchLine = new Label("Search 'posts' by: ");

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

        TextField searchPosts = new TextField();

        CheckBox sortTitle = new CheckBox("Sort by 'title'");
        CheckBox bodyFilter = new CheckBox("Filter body with key words:");

        TextField bodyFilterField = new TextField();

        Button searchPostsButton = new Button("Search Posts");

        Label searchComments = new Label("\nSearch 'comments' by: \n" +
                                        "Post ID: ");

        RadioButton commentPostId = new RadioButton("Post ID");
        RadioButton commentId = new RadioButton("Comment ID");
        RadioButton commentName = new RadioButton("Comment Name");
        RadioButton commentEmail = new RadioButton("Comment Email");

        ToggleGroup searchCommentsBy = new ToggleGroup();
        commentPostId.setToggleGroup(searchCommentsBy);
        commentId.setToggleGroup(searchCommentsBy);
        commentName.setToggleGroup(searchCommentsBy);
        commentEmail.setToggleGroup(searchCommentsBy);

        TextField postId = new TextField();

        HBox commentSelection = new HBox(10);
        commentSelection.getChildren().addAll(commentPostId, commentId, commentName, commentEmail);

        TextField searchCommentsField = new TextField();

        Button searchCommentsButton = new Button("Search Comments");

        Label searchResult = new Label("\nResluts:");
        VBox result = new VBox();
        result.setMaxHeight(180);
        ScrollPane resultsPane = new ScrollPane();
        resultsPane.setPadding(new Insets(5,5,5,5));

        Text results = new Text();
        resultsPane.setContent(results);
        resultsPane.setHmax(450);
        result.getChildren().addAll(resultsPane);

        Button exportData = new Button("Export to CSV");

        searchPostsButton.setOnAction(e -> {

            String search = searchPosts.getText();
            String searchTitle = search.replace(" ", "%20");


            URL urlGet = null;

            if(id.isSelected()){

                try {

                    urlGet = new URL("https://jsonplaceholder.typicode.com/posts?id="+search);

                }catch (Exception e1){}

            }if(userId.isSelected()){

                try {

                    urlGet = new URL("https://jsonplaceholder.typicode.com/posts?userId="+search);

                }catch (Exception e1){}

            }if(title.isSelected()){

                try {

                    urlGet = new URL("https://jsonplaceholder.typicode.com/posts?title="+searchTitle);

                }catch (Exception e1){}

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

                    String responseString = response.toString();

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
                                }catch (IOException e2){}

                            });

                        }else{

                            results.setText(temp.toString());

                            exportData.setOnAction(e1 -> {

                                try {
                                    export(temp, stage);
                                }catch (IOException e2){}

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
                                }catch (IOException e2){}

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

                }catch (Exception e1){}

            }if(commentId.isSelected()){

                try {

                    urlGet = new URL("https://jsonplaceholder.typicode.com/posts/" + postIdVal + "/comments?id=" + search);

                }catch (Exception e1){}

            }if(commentName.isSelected()){

                try {

                    urlGet = new URL("https://jsonplaceholder.typicode.com/posts/" + postIdVal + "/comments?name=" + searchTitle);

                }catch (Exception e1){}

            }if(commentEmail.isSelected()){

                try {

                    urlGet = new URL("https://jsonplaceholder.typicode.com/posts/" + postIdVal + "/comments?email=" + search);

                }catch (Exception e1){}

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


            }

        });

        content.getChildren().addAll(searchLine, selection, searchPosts, sortTitle, bodyFilter, bodyFilterField, searchPostsButton, searchComments, postId, commentSelection, searchCommentsField, searchCommentsButton, searchResult, result, exportData);

        return content;

    }

    private VBox postTabContent() throws IOException {

        VBox content = new VBox();

        URL urlPost = new URL("https://jsonplaceholder.typicode.com/posts/" );

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

        Label searchResult = new Label("Resluts:");
        VBox result = new VBox();
        ScrollPane resultsPane = new ScrollPane();
        resultsPane.setPadding(new Insets(5,5,5,5));

        Text results = new Text();
        resultsPane.setContent(results);
        resultsPane.setHmax(450);
        result.getChildren().addAll(resultsPane);

        post.setOnAction(e ->{

            if(titleField.getText().length() == 0){

                results.setText("Please enter a 'title' value.");

            }if(bodyField.getText().length() > 400){

                results.setText("The length of the body exceeds 400 characters.");

            }else{

                String postParams = "{\n" + "\"userID\": " + userIdField.getText() + ", \r\n" +
                        "   \"id\": " + idField.getText() + ", \r\n" +
                        "   \"title\": \"" + titleField.getText() + "\", \r\n" +
                        "   \"body\": \"" + bodyField.getText() + "\" \n}";

                //System.out.println(postParams);

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

                        results.setText("Post response code: " + responseCode + "\n\n" +
                                "Post response message: " + postConnection.getResponseMessage() + "\n\n" +
                                response.toString());

                    } else {

                        System.out.println("Post did not work");

                    }

                } catch (Exception e1) {


                }
            }

        });

        content.getChildren().addAll(userId, userIdField, id, idField, title, titleField, body, bodyField, post, searchResult, result);


        return content;

    }

    private static void export(ArrayList<Posts> posts, Stage stage) throws IOException{

        FileChooser fileChooser = new FileChooser();

        File file = fileChooser.showSaveDialog(stage);

        String filePath = null;

        if(file != null){

            filePath = file.getPath();

        }

        FileWriter fileWriter = new FileWriter(filePath);

        fileWriter.append("UserID");
        fileWriter.append(",");
        fileWriter.append("ID");
        fileWriter.append(",");
        fileWriter.append("title");
        fileWriter.append(",");
        fileWriter.append("body");
        fileWriter.append("\n");

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
