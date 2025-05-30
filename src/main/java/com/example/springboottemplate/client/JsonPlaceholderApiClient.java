package com.example.springboottemplate.client;

import com.example.springboottemplate.dto.JsonPlaceholderCommentDto;
import com.example.springboottemplate.dto.JsonPlaceholderPostDto;
import com.example.springboottemplate.dto.JsonPlaceholderTodoDto;
import com.example.springboottemplate.dto.JsonPlaceholderUserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Client for interacting with the JSONPlaceholder API
 * https://jsonplaceholder.typicode.com
 */
@Component
public class JsonPlaceholderApiClient {
    private static final Logger logger = LoggerFactory.getLogger(JsonPlaceholderApiClient.class);

    private final WebClient webClient;
    private final String apiBaseUrl;

    @Autowired
    public JsonPlaceholderApiClient(WebClient webClient, 
                           @Value("${api.jsonplaceholder.base-url:https://jsonplaceholder.typicode.com}") String apiBaseUrl) {
        this.webClient = webClient;
        this.apiBaseUrl = apiBaseUrl;
        logger.info("Initialized JsonPlaceholderApiClient with base URL: {}", apiBaseUrl);
    }

    /**
     * Get all posts
     * 
     * @return Mono containing a list of posts
     */
    public Mono<List<JsonPlaceholderPostDto>> getAllPosts() {
        logger.debug("Fetching all posts");
        
        return webClient.get()
                .uri(apiBaseUrl + "/posts")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<JsonPlaceholderPostDto>>() {})
                .doOnSuccess(posts -> logger.debug("Successfully fetched {} posts", posts.size()))
                .doOnError(error -> logger.error("Error fetching posts: {}", error.getMessage()));
    }

    /**
     * Get a post by ID
     * 
     * @param id The post ID
     * @return Mono containing the post
     */
    public Mono<JsonPlaceholderPostDto> getPostById(Long id) {
        logger.debug("Fetching post with ID: {}", id);
        
        return webClient.get()
                .uri(apiBaseUrl + "/posts/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JsonPlaceholderPostDto.class)
                .doOnSuccess(post -> logger.debug("Successfully fetched post: {}", post))
                .doOnError(error -> logger.error("Error fetching post: {}", error.getMessage()));
    }

    /**
     * Get comments for a post
     * 
     * @param postId The post ID
     * @return Mono containing a list of comments
     */
    public Mono<List<JsonPlaceholderCommentDto>> getCommentsByPostId(Long postId) {
        logger.debug("Fetching comments for post ID: {}", postId);
        
        return webClient.get()
                .uri(apiBaseUrl + "/posts/{postId}/comments", postId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<JsonPlaceholderCommentDto>>() {})
                .doOnSuccess(comments -> logger.debug("Successfully fetched {} comments for post ID: {}", comments.size(), postId))
                .doOnError(error -> logger.error("Error fetching comments: {}", error.getMessage()));
    }

    /**
     * Create a new post
     * 
     * @param post The post to create
     * @return Mono containing the created post
     */
    public Mono<JsonPlaceholderPostDto> createPost(JsonPlaceholderPostDto post) {
        logger.debug("Creating post with data: {}", post);
        
        return webClient.post()
                .uri(apiBaseUrl + "/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(post)
                .retrieve()
                .bodyToMono(JsonPlaceholderPostDto.class)
                .doOnSuccess(createdPost -> logger.debug("Successfully created post: {}", createdPost))
                .doOnError(error -> logger.error("Error creating post: {}", error.getMessage()));
    }

    /**
     * Update a post
     * 
     * @param id The post ID
     * @param post The updated post data
     * @return Mono containing the updated post
     */
    public Mono<JsonPlaceholderPostDto> updatePost(Long id, JsonPlaceholderPostDto post) {
        logger.debug("Updating post with ID: {}", id);
        
        return webClient.put()
                .uri(apiBaseUrl + "/posts/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(post)
                .retrieve()
                .bodyToMono(JsonPlaceholderPostDto.class)
                .doOnSuccess(updatedPost -> logger.debug("Successfully updated post: {}", updatedPost))
                .doOnError(error -> logger.error("Error updating post: {}", error.getMessage()));
    }

    /**
     * Delete a post
     * 
     * @param id The post ID
     * @return Mono containing void
     */
    public Mono<Void> deletePost(Long id) {
        logger.debug("Deleting post with ID: {}", id);
        
        return webClient.delete()
                .uri(apiBaseUrl + "/posts/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> logger.debug("Successfully deleted post with ID: {}", id))
                .doOnError(error -> logger.error("Error deleting post: {}", error.getMessage()));
    }

    /**
     * Get all users
     * 
     * @return Mono containing a list of users
     */
    public Mono<List<JsonPlaceholderUserDto>> getAllUsers() {
        logger.debug("Fetching all users");
        
        return webClient.get()
                .uri(apiBaseUrl + "/users")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<JsonPlaceholderUserDto>>() {})
                .doOnSuccess(users -> logger.debug("Successfully fetched {} users", users.size()))
                .doOnError(error -> logger.error("Error fetching users: {}", error.getMessage()));
    }

    /**
     * Get a user by ID
     * 
     * @param id The user ID
     * @return Mono containing the user
     */
    public Mono<JsonPlaceholderUserDto> getUserById(Long id) {
        logger.debug("Fetching user with ID: {}", id);
        
        return webClient.get()
                .uri(apiBaseUrl + "/users/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JsonPlaceholderUserDto.class)
                .doOnSuccess(user -> logger.debug("Successfully fetched user: {}", user))
                .doOnError(error -> logger.error("Error fetching user: {}", error.getMessage()));
    }

    /**
     * Get all todos
     * 
     * @return Mono containing a list of todos
     */
    public Mono<List<JsonPlaceholderTodoDto>> getAllTodos() {
        logger.debug("Fetching all todos");
        
        return webClient.get()
                .uri(apiBaseUrl + "/todos")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<JsonPlaceholderTodoDto>>() {})
                .doOnSuccess(todos -> logger.debug("Successfully fetched {} todos", todos.size()))
                .doOnError(error -> logger.error("Error fetching todos: {}", error.getMessage()));
    }

    /**
     * Get todos for a user
     * 
     * @param userId The user ID
     * @return Mono containing a list of todos
     */
    public Mono<List<JsonPlaceholderTodoDto>> getTodosByUserId(Long userId) {
        logger.debug("Fetching todos for user ID: {}", userId);
        
        return webClient.get()
                .uri(apiBaseUrl + "/users/{userId}/todos", userId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<JsonPlaceholderTodoDto>>() {})
                .doOnSuccess(todos -> logger.debug("Successfully fetched {} todos for user ID: {}", todos.size(), userId))
                .doOnError(error -> logger.error("Error fetching todos: {}", error.getMessage()));
    }
}
