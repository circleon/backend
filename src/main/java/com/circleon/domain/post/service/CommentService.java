package com.circleon.domain.post.service;

import com.circleon.common.dto.PaginatedResponse;
import com.circleon.domain.post.dto.*;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    CommentCreateResponse createComment(RequestIdentifiers identifiers, CommentCreateRequest commentCreateRequest);

    PaginatedResponse<CommentSearchResponse> findPagedComments(RequestIdentifiers identifiers, Pageable pageable);

    CommentUpdateResponse updateComment(RequestIdentifiers identifiers, Long commentId, CommentUpdateRequest commentUpdateRequest);

    void deleteComment(RequestIdentifiers identifiers, Long commentId);
}
