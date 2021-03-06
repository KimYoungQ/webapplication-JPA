package com.webapplication.content;

import com.webapplication.account.Account;
import com.webapplication.account.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ContentRepository contentRepository;

    @DisplayName("공지사항 메인 화면 테스트")
    @Test
    public void main() throws Exception {
        mockMvc.perform(get("/")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("contentPage"));
    }

    @DisplayName("공지글 조회")
    @WithMockCustomUser("testName")
    @Test
    public void read() throws Exception {
        Content content = createContent("testName");
        String content_id = longToString(content.getId());

        mockMvc.perform(get("/post/read")
                .param("content_id", content_id)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("post/read"))
                .andExpect(model().attributeExists("content"))
                .andExpect(authenticated());

    }

    @DisplayName("공지글 작성")
    @WithMockCustomUser("testName")
    @Test
    public void create() throws Exception {
        MockMultipartFile jsonFile = new MockMultipartFile("test.json", "test", "application/json", "{\"key1\": \"value1\"}".getBytes());

        mockMvc.perform(multipart("/post/write")
                .file("files", jsonFile.getBytes())
                .param("subject", "제목 테스트")
                .param("text", "내용테스트")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/post/read"))
                .andExpect(model().attributeExists("content_id"))
                .andExpect(authenticated());

        List<Content> all = contentRepository.findAll();
        assertThat(all.size()).isEqualTo(1);
    }

    @DisplayName("공지글 수정")
    @WithMockCustomUser("testName")
    @Test
    public void modify() throws Exception {
        Content content = createContent("testName");
        String content_id = longToString(content.getId());
        String date = dateToString(content.getDate());

        content.setModifiedDate(new Date());
        String modifiedDate = dateToString(content.getModifiedDate());


        mockMvc.perform(post("/post/modify")
                .param("subject", "제목 수정 테스트")
                .param("text", "내용 수정 테스트")
                .param("content_id", content_id)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/post/read"))
                .andExpect(model().attributeExists("content_id"))
                .andExpect(authenticated());

        List<Content> all = contentRepository.findAll();
        assertThat(all.get(0).getSubject()).isEqualTo("제목 수정 테스트");
        assertThat(all.get(0).getText()).isEqualTo("내용 수정 테스트");

    }

    @DisplayName("공지글 삭제")
    @WithMockCustomUser("testName")
    @Test
    public void delete() throws Exception {
        Content content = createContent("testName");
        String content_id = longToString(content.getId());

        mockMvc.perform(get("/post/delete")
                .param("content_id", content_id)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        List<Content> all = contentRepository.findAll();
        assertThat(all.size()).isEqualTo(0);
    }

    private String dateToString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }


    private String longToString(Long id) {
        String content_id = String.valueOf(id);
        return content_id;
    }

    private Content createContent(String testName) {
        Account selectedAccount = accountRepository.findByName(testName);

        Content content = Content.builder()
                .subject("제목 테스트")
                .text("내용 테스트")
                .date(new Date())
                .modifiedDate(new Date())
                .writer(selectedAccount)
                .build();

        Content saveContent = contentRepository.save(content);

        contentRepository.findAll();

        return saveContent;
    }

}