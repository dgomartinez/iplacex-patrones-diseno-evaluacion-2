package cl.patrones.taller.u2.tienda.menu;

import java.util.List;

import cl.patrones.taller.u2.catalogo.domain.Categoria;
import cl.patrones.taller.u2.tienda.menu.util.Slugger;

public class CategoriaMenu implements ItemMenu {

	private Categoria categoria;
	private String slug;
	private List<ItemMenu> hijos;

	public CategoriaMenu(Categoria categoria, List<ItemMenu> hijos) {
		super();
		this.categoria = categoria;
		this.slug = Slugger.toSlug(categoria.getNombre());
		this.hijos = hijos == null ? List.of() : List.copyOf(hijos);
	}

	@Override
	public String getTexto() {
		return categoria.getNombre();
	}

	@Override
	public String getSlug() {
		return slug;
	}

	@Override
	public String getEnlace() {
		return "/categoria/" + categoria.getId() + "/" + slug;
	}

	@Override
	public boolean tieneHijos() {
		return !hijos.isEmpty();
	}

	@Override
	public List<ItemMenu> getHijos() {
		return hijos;
	}

}
