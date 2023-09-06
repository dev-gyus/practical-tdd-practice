package sample.cafekiosk.spring.api.service.mail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import sample.cafekiosk.spring.api.controller.client.MailSendClient;
import sample.cafekiosk.spring.domain.mail.MailSendHistory;
import sample.cafekiosk.spring.domain.mail.MailSendHistoryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {
    @Mock
    MailSendClient mailSendClient;
    @Mock
    MailSendHistoryRepository mailSendHistoryRepository;
    @InjectMocks
    MailService mailService;
//;;
    @DisplayName("메일 전송 테스트")
    @Test
    public void sendMail() throws Exception{
        //given
        when(mailSendClient.sendMail(anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        when(mailSendHistoryRepository.save(any())).thenReturn(null);

        //when
        boolean result = mailService.sendMail("", "", "", "");

        //then
        assertThat(result).isTrue();
        verify(mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class));

    }

}