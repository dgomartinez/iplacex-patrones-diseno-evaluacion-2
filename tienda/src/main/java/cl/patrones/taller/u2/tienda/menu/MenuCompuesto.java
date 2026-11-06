package cl.patrones.taller.u2.tienda.menu;

import java.util.List;

public class MenuCompuesto implements ItemMenu {

	private String texto;
	private String slug;
	private String enlace;
	private List<ItemMenu> hijos;

	public MenuCompuesto(String texto, String slug, String enlace, List<ItemMenu> hijos) {
		super();
		this.texto = texto;
		this.slug = slug;
		this.enlace = enlace;
		this.hijos = hijos == null ? List.of() : List.copyOf(hijos);
	}

	@Override
	public String getTexto() {
		return texto;
	}

	@Override
	public String getSlug() {
		return slug;
	}

	@Override
	public String getEnlace() {
		return enlace;
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
