package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("title", "Menus");
        model.addAttribute("menus", menuDao.findAll());

        return "menu/index";
    }

    @RequestMapping(value = "add", method= RequestMethod.GET)
    public String displayAddMenu(Model model) {

        model.addAttribute("menu", new Menu());
        model.addAttribute("title", "Add Menu");
        return "menu/add";
    }

    @RequestMapping(value = "add", method= RequestMethod.POST)
    public String processAddMenu(Model model, @ModelAttribute @Valid Menu newMenu, Errors errors) {


        if ( errors.hasErrors() ) {

            model.addAttribute("title", "Add Menu");
            model.addAttribute("errors", errors);
            model.addAttribute("menu", new Menu());

        }

        menuDao.save(newMenu);

        return "redirect:view/" + newMenu.getId();
    }

    @RequestMapping(value="view/{id}", method=RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int id) {

        model.addAttribute("title", "View Menu: " + menuDao.findOne(id).getName());
        model.addAttribute("menu", menuDao.findOne(id));

        return "menu/view";
    }

    @RequestMapping(value="add-item/{id}", method=RequestMethod.GET)
    public String addItem(Model model, @PathVariable int id) {

        AddMenuItemForm form = new AddMenuItemForm(menuDao.findOne(id), cheeseDao.findAll() );

        model.addAttribute("form", form);
        model.addAttribute("title", "Add item to menu: " + menuDao.findOne(id).getName());

        return "menu/add-item";
    }

    @RequestMapping(value="add-item", method=RequestMethod.POST)
    public String processAdditem(Model model,
                                 @ModelAttribute @Valid AddMenuItemForm form, Errors errors) {

        if (errors.hasErrors() ) {
            model.addAttribute("form", form);

            return "menu/add-item";
        }

        Cheese theCheese = cheeseDao.findOne(form.getCheeseId());
        Menu theMenu = menuDao.findOne(form.getMenuId());
        theMenu.addItem(theCheese);
        menuDao.save(theMenu);

        return "redirect:/menu/view/" + theMenu.getId();
    }

}
