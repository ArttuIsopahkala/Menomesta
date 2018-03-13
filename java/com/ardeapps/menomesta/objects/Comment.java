package com.ardeapps.menomesta.objects;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arttu on 25.9.2016.
 */
public class Comment {

    public String commentId;
    public String userId;
    public long time;
    public String message;
    public long replySize;
    public List<String> usersVoted;
    public List<String> usersReported;

    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    @Exclude
    public static ArrayList<Comment> setComment(ArrayList<Comment> comments, String commentId, Comment comment) {
        if (comments == null)
            comments = new ArrayList<>();

        boolean exists = false;
        for (int index = 0; index < comments.size(); index++) {
            if (comments.get(index).commentId.equals(commentId)) {
                exists = true;
                if (comment == null) {
                    comments.remove(index);
                } else {
                    comments.set(index, comment);
                }
                break;
            }
        }
        if (!exists && comment != null) {
            comments.add(comment);
        }
        return comments;
    }

    @Exclude
    public Comment clone() {
        Comment clone = new Comment();
        clone.commentId = this.commentId;
        clone.userId = this.userId;
        clone.time = this.time;
        clone.message = this.message;
        clone.replySize = this.replySize;
        if (this.usersVoted == null) {
            clone.usersVoted = new ArrayList<>();
        } else {
            clone.usersVoted = this.usersVoted;
        }
        if (this.usersReported == null) {
            clone.usersReported = new ArrayList<>();
        } else {
            clone.usersReported = this.usersReported;
        }
        return clone;
    }
}
