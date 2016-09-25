package edu.virginia.cs.index;

public class ResultDoc {

    private final int _id;
    private String _content = "[no content]";
    private String _url = "[no url]";
    private String _topic = "[no topic]";

    public ResultDoc(int id) {
        _id = id;
    }

    public int id() {
        return _id;
    }

    public String content() {
        return _content;
    }

    public ResultDoc content(String content) {
        _content = content;
        return this;
    }

    public String url() {
        return _url;
    }

    public ResultDoc url(String url) {
        _url = url;
        return this;
    }

    public String topic() {
        return _topic;
    }

    public ResultDoc topic(String topic) {
        _topic = topic;
        return this;
    }
}
