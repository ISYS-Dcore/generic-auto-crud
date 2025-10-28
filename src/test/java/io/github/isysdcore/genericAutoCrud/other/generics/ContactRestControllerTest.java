//package io.github.isysdcore.genericAutoCrud.other.generics;
//
//import com.isysdcore.smsg.SMSGApplication;
//import com.isysdcore.smsg.generics.AuthenticationManagement;
//import com.isysdcore.smsg.generics.GenericRestAbstractIntegrationTests;
//import com.isysdcore.smsg.services.AuthService;
//import com.isysdcore.smsg.user.EAccountStatus;
//import com.isysdcore.smsg.user.ERoles;
//import com.isysdcore.smsg.user.User;
//import com.isysdcore.smsg.util.Consts;
//import com.isysdcore.smsg.util.records.JwtResponse;
//import io.github.isysdcore.genericAutoCrud.generics.model.TestProperties;
//import io.github.isysdcore.genericAutoCrud.utils.Constants;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MvcResult;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
///**
// * @author domingos.fernando
// * @created 26/12/2024 - 11:08
// * @project smsg
// */
//@SpringBootTest(classes = SMSGApplication.class)
//@AutoConfigureMockMvc
//public class ContactRestControllerTest extends GenericRestAbstractIntegrationTests<Contact, ContactServiceImpl> implements AuthenticationManagement {
//
//    private static final Logger log = LoggerFactory.getLogger(ContactRestControllerTest.class);
//    @Autowired
//    private AuthService authService;
//
//    public ContactRestControllerTest() {
//        super(new Contact(), TestProperties.builder()
//                .auth(true)
//                .username(Constants.TEST_USER_NAME)
//                .userPassword(Constants.TEST_PASSWD)
//                .resourceUrl(Constants.FULL_API_URL + "/contact")
//                .build());
//    }
//
//    @Override
//    public void prepareNewUser() {
//        try{
//            authService.loadUserInfo(Consts.TEST_USER_NAME);
//        }catch (Exception e){
//            User user = new User();
//            user.setName("FirstName SecondName");
//            user.setEmail(Consts.TEST_USER_NAME);
//            user.setPassword(Consts.TEST_PASSWD);
//            user.setAccountStatus(EAccountStatus.ACTIVE);
//            user.setRoles(ERoles.ADMIN_USER);
//            authService.createNewUser(user);
//        }
//    }
//
//    @Override
//    public String authenticateUser(String userName, String password) throws Exception  {
//        Map<String, Object> loginRequest = new HashMap<>();
//        loginRequest.put("username", userName);
//        loginRequest.put("password", password);
//        loginRequest.put("rememberMe", false);
//
//        MvcResult loginResult = getMockMvc().perform(post(Consts.FULL_AUTH_URL + "/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(getObjectMapper().writeValueAsString(loginRequest)))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andReturn();
//        String json = loginResult.getResponse().getContentAsString();
//        return getObjectMapper().readValue(json, JwtResponse.class).token();
//    }
//
//
//    @Override
//    public void authenticationManagement() throws Exception {
//        prepareNewUser();
//        setBearerJwToken(authenticateUser(Consts.TEST_USER_NAME, Consts.TEST_PASSWD));
//    }
//
//}
