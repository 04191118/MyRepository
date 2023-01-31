package com.clm.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.clm.reggie.common.R;
import com.clm.reggie.entity.User;
import com.clm.reggie.service.UserService;
import com.clm.reggie.utils.SMSUtils;
import com.clm.reggie.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();

        if (phone != null) {

            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            SMSUtils.sendMessage(phone, code);

            session.setAttribute(phone, code);

            return R.success("手机验证码短信发送成功");
        }
        return R.error("手机验证码短信发送失败");
    }

    /**
     * 用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {

        String phone = map.get("phone").toString();

        String code = map.get("code").toString();

        Object codeInSession = session.getAttribute(phone);

        if(codeInSession != null && codeInSession.equals(code)){
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if(user == null){
                Random random = new Random();
                int i = random.nextInt(90000) + 10000;
                user = new User();
                user.setPhone(phone);
                user.setName("用户名"+ i);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }

        return R.error("手机验证码短信发送失败");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest req){
        req.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}
