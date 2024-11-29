package com.kosta.geekku.controller;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.geekku.config.auth.PrincipalDetails;
import com.kosta.geekku.dto.OnestopAnswerDto;
import com.kosta.geekku.dto.OnestopDto;
import com.kosta.geekku.service.OnestopService;
import com.kosta.geekku.util.PageInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OnestopController {

	private final OnestopService onestopService;

	@PostMapping("/onestopWrite")
	public ResponseEntity<String> makeAccount(OnestopDto onestopDto) {
		try {
			Integer onestopNum = onestopService.onestopWrite(onestopDto);
			return new ResponseEntity<String>(String.valueOf(onestopNum), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	/*
	 * @PostMapping("/onestopModify") public ResponseEntity<Integer>
	 * onestopModify(OnestopDto onestopDto)
	 * 
	 * @RequestPart(value = "delFile", required = false) Integer[] delFileNum,
	 * 
	 * @RequestPart(value = "file", required = false) MultipartFile[] fileList) {
	 * try { onestopService.onestopModify(onestopDto); return new
	 * ResponseEntity<Integer>(onestopDto.getOnestopNum(), HttpStatus.OK); } catch
	 * (Exception e) { e.printStackTrace(); return new
	 * ResponseEntity<Integer>(HttpStatus.BAD_REQUEST); } }
	 */

	@GetMapping("/onestopDetail/{num}")
	public ResponseEntity<OnestopDto> onestopDetail(@PathVariable Integer num) {
		try {

			OnestopDto onestopDto = onestopService.onestopDetail(num);
			return new ResponseEntity<OnestopDto>(onestopDto, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<OnestopDto>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/onestopList")
	public ResponseEntity<Map<String, Object>> onestopList(
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "keyword", required = false) String word) {
		try {
			PageInfo pageInfo = new PageInfo();
			pageInfo.setCurPage(page);
			List<OnestopDto> onestopList = onestopService.onestopList(pageInfo, type, word);

			// 리스트를 내림차순으로 정렬
			onestopList.sort(Comparator.comparing(OnestopDto::getCreatedAt).reversed());

			Map<String, Object> listInfo = new HashMap<>();
			listInfo.put("onestopList", onestopList);
			listInfo.put("pageInfo", pageInfo);
			return new ResponseEntity<>(listInfo, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/onestopDelete/{num}")
	public ResponseEntity<String> onestopDelete(@PathVariable Integer num) {
		try {
			onestopService.onestopDelete(num);
			return new ResponseEntity<String>(String.valueOf(true), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	// 개인 마이페이지 - 한번에 꾸하기 신청내역
	@GetMapping("/user/mypageUserOnestopList")
	public ResponseEntity<Page<OnestopDto>> mypageUserOnestopList(
			Authentication authentication,
			@RequestParam(required = false, defaultValue = "1", value = "page") int page,
			@RequestParam(required = false, defaultValue = "10", value = "size") int size) {
		try {
			UUID userId = ((PrincipalDetails)authentication.getPrincipal()).getUser().getUserId();
			Page<OnestopDto> onestopList = onestopService.onestopListForUserMypage(page, size, userId);
			return new ResponseEntity<Page<OnestopDto>>(onestopList, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Page<OnestopDto>>(HttpStatus.OK);
		}
	}

	// �븳袁� �떟蹂�
	@PostMapping("/onestopAnswerWrite")
	public ResponseEntity<String> onestopAnswerDtoWrite(OnestopAnswerDto onestopAnswerDto,
			@RequestParam Integer onestopNum) {
		try {
			Integer onestopAnswerNum = onestopService.onestopAnswerWrite(onestopAnswerDto, onestopNum);
			return new ResponseEntity<String>(String.valueOf(onestopAnswerNum), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>("諛⑷씀�떟蹂� �벑濡� �삤瑜�", HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/onestopAnswerList")
	public ResponseEntity<Map<String, Object>> onestopAnswerList(
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam("onestopNum") Integer onestopNum) {
		try {
			PageInfo pageInfo = new PageInfo();
			pageInfo.setCurPage(page);
			List<OnestopAnswerDto> onestopAnswerList = onestopService.onestopAnswerList(pageInfo, onestopNum);
			Map<String, Object> listInfo = new HashMap<>();
			listInfo.put("onestopAnswerList", onestopAnswerList);
			listInfo.put("pageInfo", pageInfo);
			System.out.println(onestopAnswerList);

			return new ResponseEntity<Map<String, Object>>(listInfo, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/onestopAnswerDelete")
	public ResponseEntity<String> interiorAnswerDelete(@RequestParam("onestopAnswerNum") Integer onestopAnswerNum,
			@RequestParam("onestopNum") Integer onestopNum) {
		try {
			onestopService.onestopAnswerDelete(onestopAnswerNum, onestopNum);
			System.out.println(onestopAnswerNum);
			return new ResponseEntity<String>("true", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>("吏묎씀�떟蹂� �궘�젣 �삤瑜�", HttpStatus.BAD_REQUEST);
		}
	}
}
