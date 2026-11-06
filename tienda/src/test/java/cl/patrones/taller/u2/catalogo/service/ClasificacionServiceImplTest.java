package cl.patrones.taller.u2.catalogo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.patrones.taller.u2.catalogo.domain.Categoria;
import cl.patrones.taller.u2.catalogo.domain.Clasificacion;
import cl.patrones.taller.u2.catalogo.repository.ClasificacionRepository;

@ExtendWith(MockitoExtension.class)
class ClasificacionServiceImplTest {

	@Mock
	private ClasificacionRepository repository;

	@InjectMocks
	private ClasificacionServiceImpl service;

	@Test
	void obtieneLaCategoriaPorSku() {
		Categoria comedor = categoria(7L, "Comedor");
		when(repository.findFirstBySku("lamp-001"))
				.thenReturn(Optional.of(new Clasificacion("lamp-001", comedor)));

		Optional<Categoria> resultado = service.getCategoriaPorSku("lamp-001");

		assertThat(resultado).contains(comedor);
		verify(repository).findFirstBySku("lamp-001");
		verifyNoMoreInteractions(repository);
	}

	@Test
	void devuelveVacioCuandoElSkuNoExiste() {
		when(repository.findFirstBySku("inexistente")).thenReturn(Optional.empty());

		Optional<Categoria> resultado = service.getCategoriaPorSku("inexistente");

		assertThat(resultado).isEmpty();
		verify(repository).findFirstBySku("inexistente");
		verifyNoMoreInteractions(repository);
	}

	@Test
	void obtieneSoloLosSkusDeLaCategoriaExacta() {
		when(repository.findByCategoriaId(7L)).thenReturn(List.of(
				new Clasificacion("lamp-001", categoria(7L, "Comedor")),
				new Clasificacion("lamp-002", categoria(7L, "Comedor")),
				new Clasificacion("lamp-003", categoria(7L, "Comedor"))));

		List<String> skus = service.getSkusPorCategoria(7L);

		assertThat(skus).containsExactly("lamp-001", "lamp-002", "lamp-003");
		verify(repository).findByCategoriaId(7L);
		verifyNoMoreInteractions(repository);
	}

	@Test
	void noIncluyeSubcategorias() {
		// La categoría padre "Lámparas" (1) no tiene clasificaciones directas.
		// Sus hijas (Comedor 7, Dormitorio 8, etc.) no deben consultarse.
		when(repository.findByCategoriaId(1L)).thenReturn(List.of());

		List<String> skus = service.getSkusPorCategoria(1L);

		assertThat(skus).isEmpty();
		verify(repository).findByCategoriaId(1L);
		verifyNoMoreInteractions(repository);
	}

	private Categoria categoria(Long id, String nombre) {
		Categoria categoria = new Categoria();
		categoria.setId(id);
		categoria.setNombre(nombre);
		return categoria;
	}

}
