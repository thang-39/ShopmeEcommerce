package com.shopme.admin.user;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
public class UserController {

    @Autowired
    private UserService service;

//    @GetMapping("/users")
//    public ModelAndView listAll() {
//        ModelAndView mav =new ModelAndView("/users");
//        mav.addObject("listUsers",service.listAll());
//        return mav;
//    }

    @GetMapping("/users")
    public String listAll(Model model) {
        List<User> listUsers = service.listAll();
        model.addAttribute("listUsers",listUsers);
        return "users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        List<Role> listRoles = service.listRoles();
        User user = new User();
        user.setEnabled(true);

        model.addAttribute("user",user);
        model.addAttribute("listRoles",listRoles);
        model.addAttribute("pageTitle","Create New User");

        return "user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user,
                           RedirectAttributes redirectAttributes,
                           @RequestParam("image") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);

            User savedUser = service.save(user);

            String uploadDir = "user-photos/" + savedUser.getId();
            // clean all photos
            FileUploadUtil.cleanDir(uploadDir);

            // then save new updated photo
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty()) user.setPhotos(null);
            service.save(user);
        }

        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");

        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id,
                           Model model,
                           RedirectAttributes redirectAttributes) throws UserNotFoundException {

        try {
            List<Role> listRoles = service.listRoles();
            User user = service.get(id);
            model.addAttribute("user",user);
            model.addAttribute("pageTitle","Edit User (ID: " + id + ")");
            model.addAttribute("listRoles",listRoles);
            return "user_form";
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("message",
                    "The user ID " + id + " has been deleted successfully");
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/users";
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id,
                                          @PathVariable("status") boolean enabled,
                                          RedirectAttributes redirectAttributes) {
        service.updateUserEnableStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The user ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message",message);
        return "redirect:/users";
    }


}
