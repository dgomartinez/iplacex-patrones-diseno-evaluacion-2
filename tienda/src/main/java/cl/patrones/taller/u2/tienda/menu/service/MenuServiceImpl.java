package cl.patrones.taller.u2.tienda.menu.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cl.patrones.taller.u2.catalogo.domain.Categoria;
import cl.patrones.taller.u2.catalogo.service.CategoriaService;
import cl.patrones.taller.u2.tienda.menu.CategoriaMenu;
import cl.patrones.taller.u2.tienda.menu.EnlaceMenu;
import cl.patrones.taller.u2.tienda.menu.ItemMenu;
import cl.patrones.taller.u2.tienda.menu.MenuCompuesto;

@Service
public class MenuServiceImpl implements MenuService {

	private CategoriaService categoriaService;

	public MenuServiceImpl(CategoriaService categoriaService) {
		super();
		this.categoriaService = categoriaService;
	}

	@Override
	public List<ItemMenu> getMenu() {
		List<Categoria> categorias = categoriaService.getCategorias();

		Map<Long, List<Categoria>> hijosPorPadre = new HashMap<>();
		List<Categoria> categoriasRaiz = new ArrayList<>();

		for (Categoria categoria : categorias) {
			if (categoria.getPadre() == null) {
				categoriasRaiz.add(categoria);
			} else {
				hijosPorPadre
					.computeIfAbsent(categoria.getPadre().getId(), id -> new ArrayList<>())
					.add(categoria);
			}
		}

		List<ItemMenu> hijosCategorias = categoriasRaiz.stream()
			.map(categoria -> crearCategoriaMenu(categoria, hijosPorPadre))
			.toList();

		MenuCompuesto menuCategorias = new MenuCompuesto(
			"Categorías", "categorias", "/categoria", hijosCategorias
		);

		return List.of(
			new EnlaceMenu("Inicio", "inicio", "/"),
			menuCategorias,
			new EnlaceMenu("Ubicación", "ubicacion", "/ubicacion"),
			new EnlaceMenu("Contacto", "contacto", "/contacto")
		);
	}

	private ItemMenu crearCategoriaMenu(
			Categoria categoria,
			Map<Long, List<Categoria>> hijosPorPadre
	) {
		List<Categoria> hijos = hijosPorPadre.getOrDefault(categoria.getId(), List.of());

		List<ItemMenu> hijosMenu = hijos.stream()
			.map(hijo -> crearCategoriaMenu(hijo, hijosPorPadre))
			.toList();

		return new CategoriaMenu(categoria, hijosMenu);
	}

}
