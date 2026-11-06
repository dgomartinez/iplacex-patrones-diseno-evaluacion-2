package cl.patrones.taller.u2.tienda.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import cl.patrones.taller.u2.catalogo.domain.Categoria;
import cl.patrones.taller.u2.catalogo.service.CategoriaService;
import cl.patrones.taller.u2.tienda.menu.util.Slugger;
import cl.patrones.taller.u2.tienda.service.AvisoService;

@Controller
public class TiendaController {

	private final AvisoService avisoService;
	private final CategoriaService categoriaService;

	public TiendaController(
			AvisoService avisoService,
			CategoriaService categoriaService) {
		super();
		this.avisoService = avisoService;
		this.categoriaService = categoriaService;
	}

	@GetMapping("/")
	public String inicio(Model model) {
		model.addAttribute("avisos", avisoService.getAvisos());
		return "inicio";
	}
		
	@GetMapping("/categoria/{categoriaId}/{slug}")
	public String categoria(
			@PathVariable(name = "categoriaId") Long categoriaId,
			@PathVariable(name = "slug") String slug,
			Model model
	) {
		Categoria categoria = categoriaService.getCategoriaPorId(categoriaId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

		String slugEsperado = Slugger.toSlug(categoria.getNombre());
		if (!slugEsperado.equals(slug)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		model.addAttribute("avisos", avisoService.getAvisosPorCategoria(categoriaId));
		model.addAttribute("categoria", categoria);
		return "categoria";
	}
	
	@GetMapping("/ingresar")
	public String login() {
		return "login";
	}
	
	@GetMapping("/ubicacion")
	public String ubicacion() {return "ubicacion";}
	
	@GetMapping("/contacto")
	public String contacto() {return "contacto";}
	
}
