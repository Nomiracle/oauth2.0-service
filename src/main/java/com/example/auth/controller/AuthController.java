package com.example.auth.controller;


import com.example.auth.client.CommonClient;
import com.example.auth.constant.ConfigConstants;
import com.example.auth.exception.BusinessException;
import com.example.auth.message.AuthCallback;
import com.example.auth.repository.RedisUserInfoTempRep;
import com.example.auth.singleton.GsonSingleton;
import com.example.auth.message.ServerMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final ApplicationContext context;
    private final RedisUserInfoTempRep userInfoTempRep;

    @GetMapping("/auth/AuthUrl")
    @ResponseBody
    public String renderAuth(String redirectUrl, String source) throws BusinessException {

        CommonClient client = Optional.of(context.getBean(source + ConfigConstants.AUTH__CONFIG_CLIENT_SUFFIX, CommonClient.class))
                .orElseThrow(() -> new BusinessException(ServerMsg.UNSUPPORTED_AUTH));

        String state = ThreadContext.get("logId");
        String authorizeUrl = client.getAuthorize(state);
        userInfoTempRep.saveRedirectUrl(state, redirectUrl);
        userInfoTempRep.saveSource(state, source);

        log.info(authorizeUrl);
        return authorizeUrl;
    }

    @RequestMapping("/auth/callback")
    public String aliyunCallBack(AuthCallback callback) throws BusinessException {

        log.info("state:{}", callback.getState());
        String source = userInfoTempRep.getSource(callback.getState());
        CommonClient client = Optional.of(context.getBean(source + ConfigConstants.AUTH__CONFIG_CLIENT_SUFFIX, CommonClient.class))
                .orElseThrow(() -> new BusinessException(ServerMsg.UNSUPPORTED_AUTH));

        String userInfo = client.getUserInfo(callback);
        String redirectUri = userInfoTempRep.getRedirectUrl(callback.getState());


        userInfoTempRep.saveUserInfo(callback.getState(), userInfo);
        return "redirect:/" + redirectUri;
    }

    @RequestMapping("/auth/AuthInfos")
    @ResponseBody
    public String AuthInfos(String state) throws BusinessException {
        log.info("state:{}", state);
        String source = userInfoTempRep.getSource(state);
        if (StringUtils.isEmpty(source)) {
            throw new BusinessException(ServerMsg.INVALID_PARAMS);
        }
        CommonClient client = Optional.of(context.getBean(source + ConfigConstants.AUTH__CONFIG_CLIENT_SUFFIX, CommonClient.class))
                .orElseThrow(() -> new BusinessException(ServerMsg.UNSUPPORTED_AUTH));

        String rawUserInfo = userInfoTempRep.getUserInfo(state);

        userInfoTempRep.deleteTempCache(state);
        return client.getResponse(rawUserInfo);
    }

    @RequestMapping("/")
    public String userInfo(String userInfo, Model model) {
        model.addAttribute("userInfo", userInfo);

        return "index";
    }

    @RequestMapping("/auth/getAuthTypes")
    @ResponseBody
    public String authType() {
        Set<Field> fields = new HashSet<>(Arrays.asList(ConfigConstants.class.getDeclaredFields()));
        Set<Field> client_fields = fields.stream().filter(field -> field.getName().startsWith("AUTH_CLIENT_")).collect(Collectors.toSet());
        Set<String> authTypes = new HashSet<>();
        client_fields.forEach(field -> {
            try {
                authTypes.add(String.valueOf(field.get(null)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        log.info("支持的认证类型：{}", authTypes);
        return GsonSingleton.getGson().toJson(authTypes);
    }
}
