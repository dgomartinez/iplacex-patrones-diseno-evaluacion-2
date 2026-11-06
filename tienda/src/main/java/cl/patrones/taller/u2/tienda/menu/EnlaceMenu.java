package cl.patrones.taller.u2.tienda.menu;

import java.util.List;

public class EnlaceMenu implements ItemMenu {

	private String texto;
	private String slug;
	private String enlace;

	public EnlaceMenu(String texto, String slug, String enlace) {
		super();
		this.texto = texto;
		this.slug = slug;
		this.enlace = enlace;
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
		return false;
	}

	@Override
	public List<ItemMenu> getHijos() {
		return List.of();
	}

}
