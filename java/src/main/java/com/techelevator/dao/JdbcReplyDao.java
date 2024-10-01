package com.techelevator.dao;

import com.techelevator.model.request.CreateReplyDto;
import com.techelevator.model.responses.ReplyResponseDto;
import com.techelevator.model.responses.ThreadResponseDto;
import com.techelevator.model.responses.UserSnippetDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JdbcReplyDao implements ReplyDao{

    JdbcTemplate jdbcTemplate;

    JdbcReplyDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ReplyResponseDto> getReplies() {
        List<ReplyResponseDto> threads = new ArrayList<>();
        String sql = "SELECT * FROM replies JOIN users ON users.user_id = replies.user_id LEFT JOIN comment_replies ON comment_replies.reply_id = replies.reply_id ORDER BY comment_replies.parent_id DESC;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        if(results.next()){
            threads = mapRowToThread(results);
        }
        return threads;
    }

    @Override
    public ReplyResponseDto getReplyById(long id) {
        ReplyResponseDto reply = new ReplyResponseDto();
        String sql = "SELECT * FROM replies JOIN users ON users.user_id = replies.user_id WHERE reply_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if(results.next()){
            reply = mapRowToReply(results);
        }
        return reply;
    }

    @Override
    public List<ReplyResponseDto> getReplyByUser(long userId){
        List<ReplyResponseDto> replies = new ArrayList<>();
        String sql = "SELECT * FROM replies WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while(results.next()){
            replies.add(mapRowToReply(results));
        }
        return replies;
    }

    @Override
    public List<ReplyResponseDto> getPostThreads(long postId) {
        List<ReplyResponseDto> threads = new ArrayList<>();
        String sql = "SELECT * FROM replies JOIN users ON users.user_id = replies.user_id LEFT JOIN comment_replies ON comment_replies.reply_id = replies.reply_id WHERE replies.post_id = ? ORDER BY comment_replies.parent_id DESC;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql,postId);
        if(results.next()){
            threads = mapRowToThread(results);
        }
        return threads;
    }

    @Override
    public ReplyResponseDto createReply(CreateReplyDto reply) {
        return null;
    }

    @Override
    public ReplyResponseDto updateReply(long id, CreateReplyDto reply) {
        return null;
    }

    private List<ReplyResponseDto> mapRowToThread(SqlRowSet results) {
        Map<Long, ReplyResponseDto> replyMap = new HashMap<>();
        List<ReplyResponseDto> rootReplies = new ArrayList<>();

        // store first result
        ReplyResponseDto reply = mapRowToReply(results);
        // store it map for future reference
        replyMap.put(reply.getId(), reply);
        // store the root replies that are direct replies to the post
        rootReplies.add(reply);

        // we are iterating through results
        while(results.next()){
            // get parent_id of next reply
            Long parent = results.getLong("parent_id");
            // get next reply
            reply = mapRowToReply(results);
            replyMap.put(reply.getId(), reply);
            // check if parent is null
            if(parent == null){
                rootReplies.add(reply);
                continue;
            }
            // if parent is not null
            ReplyResponseDto parentReply = replyMap.get(parent);
            if(parentReply != null) {
                parentReply.addReplies(reply);
            }
        }
        return rootReplies;
    }

    private ReplyResponseDto mapRowToReply(SqlRowSet row){
        ReplyResponseDto reply = new ReplyResponseDto();
        reply.setId(row.getInt("reply_id"));
        reply.setDescription(row.getString("description"));
        reply.setCreated(row.getTimestamp("time_of_creation"));
        reply.setPostId(row.getInt("post_id"));
        UserSnippetDto user = new UserSnippetDto();
        user.setId(row.getInt("user_id"));
        user.setUsername(row.getString("username"));
        reply.setUser(user);
        return reply;
    }
}
