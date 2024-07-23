# generic-mock

## Visão Geral

Projeto simples desenvolvido com Spring Boot com o objetivo de ser um template para criação de Mocks. Possui um Dockerfile configurado para gerar uma native-image diminuindo o uso de recursos.

## Como registrar um handler? (Mock de um endpoint)

Basta registrar uma nova classe `@Component` (ou outra anotação que adiciona a classe ao contexto do Spring) que implementa a interface `RequestHandler` ou que extenda a classe `BaseHandler` (Para ter acesso direto ao `ResourceLoaderUtil`).
Ex: 
```
    @Component // Essa classe de fato existe, sinta-se a vontade para explora-la: com.github.vitorweirich.genericmock.handlers.PostTestHandlerImpl
	public class PostTestHandlerImpl extends BaseHandler {
		
		private static final Pattern HANDLER_PATTERN = Pattern.compile("(?<method>.*)_/v1/(?<apiName>.*)/batch/(?<operationId>.*)");
		
		public PostTestHandlerImpl(ResourceLoaderUtil resourceLoaderUtil) {
			super(resourceLoaderUtil);
		}
		@Override
		public Function<RequestDetailsDTO, ResponseEntity<Object>> getHandler() {
			return request -> {
				String method = request.getNamedGroup("method", "GET");
	            Optional<String> apiName =  request.getNamedGroup("apiName");
	            String operationId = request.getNamedGroup("operationId", "");
	            
	            System.out.println();
	            System.out.println(request.getMatchedBy());
	            System.out.println(method);
	            System.out.println(apiName);
	            System.out.println(operationId);
	            System.out.println();
				
				if("GET".equals(method)) {
					return ResponseEntity.ok("GetResponse"); 
				}
				
				Optional<Object> resource = this.resourceLoaderUtil.loadResource("mock.json", Object.class, stringBody -> stringBody.formatted(operationId));
				
				if(resource.isEmpty()) {
					return ResponseEntity.noContent().build();
				}
				return ResponseEntity.ok(resource.get());
			};
		}
	
		@Override
		public String getPathMatcher() {
			return "GET_/test";
		}
	
		@Override
		public Pattern getPatternMatcher() {
			return HANDLER_PATTERN;
		}
	
	}
```

# Utils (Facilitadores)

## RequestDetailsDTO
Expõe todas as informações relevantes sobre a request e mais alguns metodos utilitários
- getMatchedBy() -> Retorna o tipo de match (PATH ou PATTERN)
- getNamedGroup(String groupName) -> Caso o match tenha sido do tipo MatchType.PATTERN retorna o grupo da regexp especificado por parâmetro

## ResourceLoaderUtil
Permite ler e manipular arquivos lidos da paste resources/mocks no classpath do projeto
ex: 
```
// Lê um arquivo 'resources/mocks/mock.json', aplica um replace pelo parâmetro fornecido e da parse para classe Object
Optional<Object> resource = this.resourceLoaderUtil.loadResource("mock.json", Object.class, stringBody -> stringBody.formatted(operationId));
```
