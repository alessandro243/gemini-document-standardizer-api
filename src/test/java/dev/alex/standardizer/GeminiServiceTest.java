package dev.alex.standardizer;

import dev.alex.standardizer.config.GeminiProperties;
import dev.alex.standardizer.service.GeminiService;
import dev.alex.standardizer.web.dto.request.GeminiRequestDto;
import dev.alex.standardizer.web.dto.response.GeminiResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeminiServiceTest{

    @Mock
    private RestClient restClient;
    @Mock
    private GeminiProperties properties;
    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private RestClient.RequestBodySpec requestBodySpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private GeminiService service;

    @Test
    void shouldCallGeminiApiSuccessfully(){
        ArgumentCaptor<GeminiRequestDto> captor = ArgumentCaptor.forClass(GeminiRequestDto.class);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(GeminiRequestDto.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(GeminiResponseDto.class)).thenReturn(new GeminiResponseDto());

        service.callGemini(new GeminiRequestDto(), "Texto teste");
        verify(requestBodySpec).body(captor.capture());
        GeminiRequestDto envited = captor.getValue();


        Assertions.assertNotNull(envited);
        assertEquals("Texto teste", envited.getContents().get(0).getParts().get(0).getText());;
        String text = envited.getContents().getFirst().getParts().getFirst().getText();
    }
}