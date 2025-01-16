package com.circleon.domain.post.repository;


import com.circleon.domain.post.entity.PostImage;

import java.util.List;

public interface PostImageRepositoryCustom {

    void deletePostImages(List<PostImage> postImages);
}
