public class Posts implements Comparable<Posts>{

    private int userId;
    private int id;
    private String title;
    private String body;

    /**
     * Posts class
     * The JSON objects are converted to the Java objects based on this class
     */
    public Posts(){

        userId = 0;
        id = 0;
        title = null;
        body = null;

    }

    /**
     * Get the userId
     * @return userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Set userId
     * @param userId
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Get id
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set id
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set title
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get body
     * @return body
     */
    public String getBody() {
        return body;
    }

    /**
     * set body
     * @param body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * To print the Posts object
     * @return String of the Posts object
     */
    @Override
    public String toString(){

        return "userID: " + userId + "\n" +
                "ID: " + id + "\n" +
                "title: " + title + "\n" +
                "body: " + body + "\n\n";

    }

    /**
     * Compare the title of one posts object to another
     * Used for sorting the Posts in ascending order depending on their title
     * @param p Posts object to compare with
     * @return The Posts object with the title in ascending order
     */
    @Override
    public int compareTo(Posts p){

        return this.getTitle().compareTo(p.getTitle());

    }
}
