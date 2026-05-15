package dev.alex.standardizer;

import dev.alex.standardizer.config.GeminiProperties;
import dev.alex.standardizer.service.GeminiService;
import dev.alex.standardizer.web.dto.request.GeminiRequestDto;
import dev.alex.standardizer.web.dto.response.GeminiResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
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
    @DisplayName("Testa se a api consegue fazer o caminho completo.")
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
        assertEquals("Texto teste", envited.getContents().get(0).getParts().get(0).getText());;
    }

    @Test
    @DisplayName("Deve lançar exceção quando a API do Gemini retornar erro 500")
    void shouldThrowExceptionWhenGeminiApiReturns500(){
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(GeminiRequestDto.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenThrow(
                new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        Assertions.assertThrows(HttpServerErrorException.class, () -> {
            service.callGemini(new GeminiRequestDto(), "Olá, Gemini?");
        });

    }

    @Test
    @DisplayName("Deve lançar exceção quando a API do Gemini retornar erro 403 (Forbidden)")
    void shouldThrowExceptionWhenGeminiApiReturns403(){
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(GeminiRequestDto.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));

        Assertions.assertThrows(HttpClientErrorException.class, () ->
                service.callGemini(new GeminiRequestDto(), "Olá Gemini"));
    }

    @Test
    @DisplayName("Deve lançar exceção de timeout quando a API demorar demais para responder")
    void shouldThrowExceptionWhenGeminiApiTimesOut(){
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(GeminiRequestDto.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenThrow(new ResourceAccessException("Read time out"));

        Assertions.assertThrows(ResourceAccessException.class, () ->
                service.callGemini(new GeminiRequestDto(),"Este prompt é muito longo e causou timeout."));
    }

    @Test
    @DisplayName("Deve lançar uma exceção caso o prompt seja nulo.")
    void shouldThrowException_WhenPromptIsNull(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {service.callGemini(new GeminiRequestDto(), null);});
    }

    @Test
    @DisplayName("Deve lançar uma exceção caso o prompt esteja vazio.")
    void shouldThrowException_WhenPromptIsEmpty(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.callGemini(new GeminiRequestDto(), ""));
    }

    @Test
    @DisplayName("deve lançar uma exceção caso o prompt tenha apenas espaços.")
    void shouldThrowException_WhenPromptIsBlank(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.callGemini(new GeminiRequestDto(), " "));
    }
}