package com.studyolle.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.Tag.TagRepository;
import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.domain.Zone;
import com.studyolle.settings.form.TagForm;
import com.studyolle.settings.form.ZoneForm;
import com.studyolle.zone.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.studyolle.settings.SettingsController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    AccountService accountService;

    @Autowired
    ZoneRepository zoneRepository;

    private final String zoneStr = "Anseong(안성시)/Gyeonggi";

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("test")
    @DisplayName("프로필 수정 폼")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + PROFILE))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("test")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PROFILE))
                .andExpect(flash().attributeExists("message"));

        Account test = accountRepository.findByNickname("test");
        assertEquals(bio, test.getBio());
    }

    @WithAccount("test")
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우.";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PROFILE))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account test = accountRepository.findByNickname("test");
        assertNull(test.getBio());
    }

    @WithAccount("test")
    @DisplayName("비밀번호 수정 폼")
    @Test
    void updatePasswordForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + PASSWORD))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("test")
    @DisplayName("비민번호 수정하기 - 입력값 정상")
    @Test
    void updatePassword() throws Exception {
        String newPassword = "asdfasdf";
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                        .param("newPassword", newPassword)
                        .param("newPasswordConfirm", newPassword)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PASSWORD))
                .andExpect(flash().attributeExists("message"));

        Account test = accountRepository.findByNickname("test");
        assertTrue(passwordEncoder.matches(newPassword, test.getPassword()));
    }

    @WithAccount("test")
    @DisplayName("비밀번호 수정하기 - 입력값 길이 미만")
    @Test
    void updatePassword_error1() throws Exception {
        String newPassword = "asdf";
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                        .param("newPassword", newPassword)
                        .param("newPassword", newPassword)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PASSWORD))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("test")
    @DisplayName("비밀번호 수정하기 - 입력값 일치하지 않음")
    @Test
    void updatePassword_error2() throws Exception {
        String newPassword = "asdfasdf";
        String newPasswordConfirm = "asdfasdd ";
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                        .param("newPassword", newPassword)
                        .param("newPasswordConfirm", newPasswordConfirm)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PASSWORD))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("test")
    @DisplayName("닉네임 수정 폼")
    @Test
    void updateNicknameForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + ACCOUNT))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + ACCOUNT))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("test")
    @DisplayName("닉네임 수정하기 - 입력값 정상")
    @Test
    void updateNickname() throws Exception {
        String newNickname = "test2";
        mockMvc.perform(post(ROOT + SETTINGS + ACCOUNT)
                        .param("nickname", newNickname)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + ACCOUNT))
                .andExpect(flash().attributeExists("message"));

        Account test = accountRepository.findByNickname(newNickname);
        assertNotNull(test);
    }

    @WithAccount("test")
    @DisplayName("닉네임 수정하기 - 입력값 정상")
    @Test
    void updateNickname_error() throws Exception {
        String newNickname = "test";
        mockMvc.perform(post(ROOT + SETTINGS + ACCOUNT)
                        .param("nickname", newNickname)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + ACCOUNT))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));

        Account test = accountRepository.findByNickname(newNickname);
    }

    @WithAccount("test")
    @DisplayName("태그 수정 폼")
    @Test
    void updateTagForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + TAGS))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + TAGS))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithAccount("test")
    @DisplayName("계정에 태그 추가")
    @Test
    void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        Account test = accountRepository.findByNickname("test");
        assertTrue(test.getTags().contains(newTag));
    }

    @WithAccount("test")
    @DisplayName("계정에 태그 삭제")
    @Test
    void removeTag() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(test, newTag);
        assertTrue(test.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf())).andExpect(status().isOk());

        assertFalse(test.getTags().contains(newTag));
    }

    @WithAccount("test")
    @DisplayName("활동 지역 수정 폼")
    @Test
    void updateZoneForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + ZONES))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + ZONES))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"));
    }

    @WithAccount("test")
    @DisplayName("계정에 활동 지역 추가")
    @Test
    void addZone() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(zoneStr);

        mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCity(), zoneForm.getProvince());
        Account test = accountRepository.findByNickname("test");
        assertTrue(test.getZones().contains(zone));
    }

    @WithAccount("test")
    @DisplayName("계정에 활동 지역 삭제")
    @Test
    void removeZone() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(zoneStr);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCity(), zoneForm.getProvince());

        Account test = accountRepository.findByNickname("test");
        accountService.addZone(test, zone);
        assertTrue(test.getZones().contains(zone));

        mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf())).andExpect(status().isOk());

        assertFalse(test.getZones().contains(zone));
    }
}