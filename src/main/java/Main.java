import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage mainWindow;

    public static void main(String[] args) throws IOException {

        launch(args);

    }

    public void start(Stage primaryStage) throws IOException{

        mainWindow = primaryStage;

        BorderPane borderPane = new BorderPane();

        TabPane tabPane = new TabPane();

        Tab search = new Tab("Search");
        search.setClosable(false);
        VBox searchContent = searchTabContent();
        searchContent.setPadding(new Insets(10,10,10,10));
        search.setContent(searchContent);

        Tab post = new Tab("Post");
        post.setClosable(false);
        VBox postContent = postTabContent();
        postContent.setPadding(new Insets(10,10,10,10));
        post.setContent(postContent);

        Tab sortTitle = new Tab("Sort by Title");
        sortTitle.setClosable(false);
        VBox sortTitleContent = sortTitleContent();
        sortTitleContent.setPadding(new Insets(10,10,10,10));
        sortTitle.setContent(sortTitleContent);


        tabPane.getTabs().addAll(search, post, sortTitle);

        borderPane.setCenter(tabPane);

        Scene scene = new Scene(borderPane, 500, 600);

        mainWindow.setScene(scene);

        mainWindow.show();

        //getRequest();

        //postRequest();

    }

    private VBox searchTabContent(){

        VBox content = new VBox(10);
        content.setMaxSize(500,600);

        Label searchLine = new Label("Search 'posts' by: ");

        RadioButton id = new RadioButton("ID");
        RadioButton userId = new RadioButton("userID");
        RadioButton title = new RadioButton("title");

        ToggleGroup searchBy = new ToggleGroup();
        id.setToggleGroup(searchBy);
        userId.setToggleGroup(searchBy);
        title.setToggleGroup(searchBy);

        HBox selection = new HBox(10);
        selection.getChildren().addAll(id, userId, title/*, commentPostId, commentId, commentName, cemmentEmail*/);

        TextField searchPosts = new TextField();

        Button searchPostsButton = new Button("Search Posts");

        Label searchComments = new Label("Search 'comments' by: ");

        RadioButton commentPostId = new RadioButton("Post ID");
        RadioButton commentId = new RadioButton("Comment ID");
        RadioButton commentName = new RadioButton("Comment Name");
        RadioButton commentEmail = new RadioButton("Comment Email");

        ToggleGroup searchCommentsBy = new ToggleGroup();
        commentPostId.setToggleGroup(searchCommentsBy);
        commentId.setToggleGroup(searchCommentsBy);
        commentName.setToggleGroup(searchCommentsBy);
        commentEmail.setToggleGroup(searchCommentsBy);

        TextField postId = new TextField("Post ID");

        HBox commentSelection = new HBox(10);
        commentSelection.getChildren().addAll(commentPostId, commentId, commentName, commentEmail);

        TextField searchCommentsField = new TextField();

        Button searchCommentsButton = new Button("Search Comments");


        Label searchResult = new Label("Resluts:");
        VBox result = new VBox();
        ScrollPane resultsPane = new ScrollPane();

        Text results = new Text();
        resultsPane.setContent(results);
        resultsPane.setHmax(450);
        result.getChildren().addAll(resultsPane);

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

                    System.out.println("Result: " + response.toString());
                    results.setText(response.toString());


                } else {

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



        content.getChildren().addAll(searchLine, selection, searchPosts, searchPostsButton, searchComments, postId, commentSelection, searchCommentsField, searchCommentsButton, searchResult, result);


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

        Text results = new Text();
        resultsPane.setContent(results);
        resultsPane.setHmax(450);
        result.getChildren().addAll(resultsPane);

        post.setOnAction(e ->{

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

            }catch (Exception e1){


            }

        });

        content.getChildren().addAll(userId, userIdField, id, idField, title, titleField, body, bodyField, post, searchResult, result);


        return content;

    }

    private VBox sortTitleContent(){

        VBox content = new VBox(10);

        RadioButton sortAll = new RadioButton("Sort all");
        RadioButton sortId = new RadioButton("Sort by User ID:");

        ToggleGroup sortBy = new ToggleGroup();
        sortAll.setToggleGroup(sortBy);
        sortId.setToggleGroup(sortBy);

        TextField id = new TextField();

        Button sort = new Button("Sort");

        Label searchResult = new Label("Resluts:");
        VBox result = new VBox();
        ScrollPane resultsPane = new ScrollPane();

        Text results = new Text();
        resultsPane.setContent(results);
        resultsPane.setHmax(450);
        result.getChildren().addAll(resultsPane);

        sort.setOnAction(e -> {

            URL urlGet = null;

            try {

                if (sortAll.isSelected()) {

                    urlGet = new URL("https://jsonplaceholder.typicode.com/posts/");

                }
                if (sortId.isSelected()) {

                    urlGet = new URL("https://jsonplaceholder.typicode.com/posts?userId=" + id.getText());

                }

                String line = null;

                HttpURLConnection connection = (HttpURLConnection) urlGet.openConnection();
                connection.setRequestMethod("GET");
                //connection.setRequestProperty("userID", "abcdef");
                int responseCode = connection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuffer response = new StringBuffer();

                    while ((line = in.readLine()) != null) {

                        response.append(line + "\n");

                    }

                    in.close();

                    //System.out.println("Result: " + response.toString());

                    String responseString = response.toString();

                    Gson gson = new Gson();
                    Type postsListType = new TypeToken<ArrayList<Posts>>(){}.getType();
                    ArrayList<Posts> posts = gson.fromJson(responseString, postsListType);

                    Collections.sort(posts);

                    results.setText(posts.toString());

                    for(Posts p:posts){

                        System.out.println(p);
                    }

                }else{

                    System.out.println("Not worked");

                }



            }catch (Exception e1){

            }

        });

        content.getChildren().addAll(sortAll, sortId, id, sort, searchResult, result);

        return content;

    }

    public static void getRequest() throws IOException{

        URL urlGet = new URL("https://jsonplaceholder.typicode.com/posts?id=1");

        //URL urlGet = new URL("https://my-json-server.typicode.com/AfzalIsmail/restApi_test/posts/" );

        String line = null;

        HttpURLConnection connection = (HttpURLConnection) urlGet.openConnection();
        connection.setRequestMethod("GET");
        //connection.setRequestProperty("userID", "abcdef");
        int responseCode = connection.getResponseCode();

        if(responseCode == HttpURLConnection.HTTP_OK){

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer response = new StringBuffer();

            while((line = in.readLine()) != null){

                response.append(line + "\n");

            }

            in.close();

            System.out.println("Result: " + response.toString());

            String responseString = response.toString();

            Gson gson = new Gson();
            Posts object = gson.fromJson(responseString,Posts.class);

            System.out.println(object.getId());
            System.out.println(object.getUserId());
            System.out.println(object.getTitle());
            System.out.println(object.getBody());



        }else{

            System.out.println("Not worked");

        }

    }

    public static void postRequest()throws IOException{

        //URL urlPost = new URL("https://jsonplaceholder.typicode.com/posts/33");

        URL urlPost = new URL("https://my-json-server.typicode.com/AfzalIsmail/restApi_test/posts/" );

        String postParams = "{\n" + "\"userID\": 101, \r\n" +
                "   \"id\": 101, \r\n" +
                "   \"title\": \"Test Title\", \r\n" +
                "   \"body\": \"Test Body\"" + "\n}";

        System.out.println(postParams);

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

        if(responseCode == HttpURLConnection.HTTP_CREATED){

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));

            String input;
            StringBuffer response = new StringBuffer();

            while ((input = bufferedReader.readLine()) != null){

                response.append(input);


            }bufferedReader.close();

            System.out.println(response.toString());

        }else{

            System.out.println("Post did not work");

        }

    }

}
