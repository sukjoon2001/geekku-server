package com.kosta.geekku.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.geekku.dto.EstateDto;
import com.kosta.geekku.service.EstateService;
import com.kosta.geekku.util.PageInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EstateController {
	
	private final EstateService estateService; 

	@Value("${upload.path}")
	private String uploadPath;

	@PostMapping("/estateWrite")
	public ResponseEntity<String> estateWrite(EstateDto estateDto, 
			@RequestParam(name="images", required=false) MultipartFile[] images) {
		try {
			Integer estateNum = estateService.estateWrite(estateDto, images == null ? null : Arrays.asList(images));
			return new ResponseEntity<String>(String.valueOf(estateNum), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>("매물 등록 오류", HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/estateDetail")
	public ResponseEntity<Map<String, Object>> estateDetail(@RequestBody Map<String, String> param) {
		try {
			Integer estateNum = Integer.parseInt(param.get("estateNum"));
			Map<String, Object> res = new HashMap<>();
			EstateDto estateDto = estateService.estateDetail(estateNum);
			res.put("estate", estateDto);
			UUID userId = UUID.fromString(param.get("userId"));
			
			//북마크
			if (userId != null) {
				boolean bookmark = estateService.checkBookmark(userId, estateNum) != null;
				res.put("bookmark", bookmark);
			}
			
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> res = new HashMap<>();
			res.put("err", "매물 상세보기 오류");
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/estateList")
	public ResponseEntity<Map<String, Object>> estateList(
			@RequestParam(value="page", required=false, defaultValue = "1") Integer page,
			@RequestParam(value="type", required=false) String type,
			@RequestParam(value="keyword", required=false) String keyword) {
		try {
			System.out.println(type);
			System.out.println("========");
			System.out.println(keyword);
			PageInfo pageInfo = new PageInfo();
			pageInfo.setCurPage(page);
			List<EstateDto> estateList = estateService.estateList(pageInfo, type, keyword);
			Map<String, Object> listInfo = new HashMap<>();
			listInfo.put("estateList", estateList);
			listInfo.put("pageInfo", pageInfo);
			
			return new ResponseEntity<Map<String,Object>>(listInfo, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String,Object>>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/estateDelete/{estateNum}")
	public ResponseEntity<String> estateDelete(@PathVariable Integer estateNum) {
		try {
			estateService.estateDelete(estateNum);
			return new ResponseEntity<String>("true", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>("매물 삭제 오류", HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/estateBookmark")
	public ResponseEntity<String> estateBookmark(@RequestParam("estateNum") String estateNum) {
		try {
//			UUID userId = UUID.fromString(((PrincipalDetails)authentication.getPrincipal()).getUser().getId());
//			boolean heart = estateService.toggleBookmark(userId, estateNum);
			return new ResponseEntity<String>("", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}
	
	
}
