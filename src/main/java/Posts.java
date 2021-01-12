public class Posts implements Comparable<Posts>{

    private int userId;
    private int id;
    private String title;
    private String body;

    public Posts(){

        userId = 0;
        id = 0;
        title = null;
        body = null;

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString(){

        return "userID: " + userId + "\n" +
                "ID: " + id + "\n" +
                "title: " + title + "\n" +
                "body: " + body + "\n\n";

    }

    @Override
    public int compareTo(Posts p){

        return this.getTitle().compareTo(p.getTitle());

    }
}
