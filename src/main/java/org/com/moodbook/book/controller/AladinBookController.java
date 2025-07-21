package org.com.moodbook.book.controller;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.book.service.impl.AladinApiServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AladinBookController {

  private final AladinApiServiceImpl aladinApiServiceImpl;

}
