package aspects;

import annotations.AddLocationHeader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
@Aspect
public class AddLocationAspect<T> {

    @Around("annotatedWithAddLocationHeader() && annotatedWithPostMapping() && returnsResponseEntity()")
    public ResponseEntity<T> addLocationHeader(final ProceedingJoinPoint joinPoint) throws Throwable {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        final AddLocationHeader addLocationHeader = method.getAnnotation(AddLocationHeader.class);

        final ResponseEntity<T> responseEntity = (ResponseEntity)joinPoint.proceed();

        // Bail out if the status is not created.
        if (!responseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
            return responseEntity;
        }

        final T t = responseEntity.getBody();
        final List<String> argValues = new ArrayList<>();
        Arrays.stream(addLocationHeader.properties()).forEach(p -> {
            try {
                final Field field = t.getClass().getDeclaredField(p);
                field.setAccessible(true);
                final String fieldData = String.valueOf(field.get(t));
                argValues.add(fieldData);
            } catch (final NoSuchFieldException|IllegalArgumentException|IllegalAccessException e) {
                // Do nothing
            }
        });

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path(addLocationHeader.uri())
                .buildAndExpand(argValues.toArray())
                .toUri();

        final HttpHeaders newHeaders = HttpHeaders.writableHttpHeaders(responseEntity.getHeaders());
        newHeaders.add("location", uri.toString());

        return new ResponseEntity<>(responseEntity.getBody(), newHeaders, HttpStatus.CREATED);
    }

    @Pointcut("execution(@annotations.AddLocationHeader * *.*(..))")
    public void annotatedWithAddLocationHeader() {

    }

    @Pointcut("execution(@org.springframework.web.bind.annotation.PostMapping * *.*(..))")
    public void annotatedWithPostMapping() {

    }
    @Pointcut("execution(org.springframework.http.ResponseEntity *.*(..))")
    public void returnsResponseEntity() {

    }
}