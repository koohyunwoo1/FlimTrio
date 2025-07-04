package com.example.flim.controller;

import java.util.List;

import com.example.flim.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.flim.dto.MovieDetailDTO;
import com.example.flim.dto.ApiResponse;
import com.example.flim.dto.ReviewResponse;
import com.example.flim.service.AuthService;
import com.example.flim.service.MovieDetailReviewService;
import com.example.flim.util.JwtUtil;

@Controller
@RequestMapping("/api/v1/movie-detail")
public class MovieDetailReviewController {
    
	@Autowired
	private  AuthService authService;
	
	@Autowired
	private MovieDetailReviewService movieDetailReviewService;
	
	@Autowired
	private JwtUtil jwtUtil;
	  
	
	  //리뷰 조회
	  @GetMapping("/view-reviews")
	  public ResponseEntity<ReviewResponse> getReviews(@RequestParam(name = "id") int id) {
		  System.out.println("뭐가왔니?"+ id);
		 List<MovieDetailDTO> data = movieDetailReviewService.getReview(id);
		    if (data != null && !data.isEmpty()) {
		        return ResponseEntity.ok(new ReviewResponse(true, "리뷰 조회 성공", data));
		    } else {
		        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ReviewResponse(false, "리뷰를 찾을 수 없습니다.", null));
		    }
    }
	  
	  //리뷰 작성
	  @PostMapping("/write-review")
	  public ResponseEntity<ApiResponse> writeReviews(@RequestHeader("Authorization") String authorizationHeader,
													                          @RequestBody MovieDetailDTO moviedetaildto) {

		    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
		        return ResponseEntity.badRequest().body(new ApiResponse(false, "유효하지 않은 토큰입니다."));
		    }
		    String jwtToken = authorizationHeader.substring(7); 
		    if (jwtToken.isEmpty()) {
		        return ResponseEntity.badRequest().body(new ApiResponse(false, "토큰이 필요합니다."));
		    }
		    String email = jwtUtil.extractUsername(jwtToken);
		    if (email == null) {
		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "유효하지 않은 토큰입니다."));
		    }

		    UserDetails user = authService.loadUserByUsername(email);
		    if (user == null) {
		        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "사용자를 찾을 수 없습니다."));
		    }
		    int user_idx = authService.getUserIdx(email);
		    
		    boolean isWrite = movieDetailReviewService.isWrite(moviedetaildto,user_idx);
		    if (isWrite) {
		    	 return ResponseEntity.ok(new ApiResponse(false, "작성된 리뷰가 있음"));
		    }
		    boolean isSuccess =  movieDetailReviewService.writeReview(moviedetaildto,user_idx);
		    
		    if (isSuccess) {
		        return ResponseEntity.ok(new ApiResponse(true, "리뷰 작성 성공"));
		    } else {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "리뷰 작성 실패"));
		    }
    }
	  
	  //리뷰 수정
	  @PatchMapping("/update-review")
      public ResponseEntity<ApiResponse> updateReviews(@RequestHeader("Authorization") String authorizationHeader,
                                                                   @RequestBody MovieDetailDTO moviedetaildto) {
	    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
		return ResponseEntity.badRequest().body(new ApiResponse(false, "유효하지 않은 토큰입니다."));
		}
	    String jwtToken = authorizationHeader.substring(7);  
		if (jwtToken.isEmpty()) {
		return ResponseEntity.badRequest().body(new ApiResponse(false, "토큰이 필요합니다."));
	    }
		String email = jwtUtil.extractUsername(jwtToken);
		if (email == null) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "유효하지 않은 토큰입니다."));
		}
			
		UserDetails user = authService.loadUserByUsername(email);
		if (user == null) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "사용자를 찾을 수 없습니다."));
		}
		int user_idx = authService.getUserIdx(email);
		
		boolean isSuccess =  movieDetailReviewService.updateReview(moviedetaildto,user_idx);
		
		if (isSuccess) {
		    return ResponseEntity.ok(new ApiResponse(true, "리뷰 수정 성공"));
		} else {
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "리뷰 수정 실패"));
		}
   }
	  
	  //리뷰 삭제
	  @DeleteMapping("/delete-review")
	  public ResponseEntity<ApiResponse> deleteReviews(@RequestHeader("Authorization") String authorizationHeader,
                                                               @RequestBody MovieDetailDTO moviedetaildto) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
		return ResponseEntity.badRequest().body(new ApiResponse(false, "유효하지 않은 토큰입니다."));
		}
		String jwtToken = authorizationHeader.substring(7);  
		if (jwtToken.isEmpty()) {
		return ResponseEntity.badRequest().body(new ApiResponse(false, "토큰이 필요합니다."));
		}
		String email = jwtUtil.extractUsername(jwtToken);
		if (email == null) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "유효하지 않은 토큰입니다."));
		}
		UserDetails user = authService.loadUserByUsername(email);
		if (user == null) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "사용자를 찾을 수 없습니다."));
		}
		int user_idx = authService.getUserIdx(email);
		
		boolean isSuccess =  movieDetailReviewService.deleteReview(moviedetaildto,user_idx);
		System.out.println(isSuccess);
		if (isSuccess) {
		    return ResponseEntity.ok(new ApiResponse(true, "리뷰 삭제 성공"));
		} else {
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "리뷰 삭제 실패"));
		}
	  }
}
