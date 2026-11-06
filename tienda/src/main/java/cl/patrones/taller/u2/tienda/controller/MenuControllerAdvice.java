package cl.patrones.taller.u2.tienda.controller;


import java.util.List;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import cl.patrones.taller.u2.tienda.menu.ItemMenu;
import cl.patrones.taller.u2.tienda.menu.service.MenuService;

@ControllerAdvice
public class MenuControllerAdvice {

	private MenuService menuService;

	public MenuControllerAdvice(MenuService menuService) {
		super();
		this.menuService = menuService;
	}

	@ModelAttribute("menu")
	public List<ItemMenu> menu() {
		return menuService.getMenu();
	}
	
}
