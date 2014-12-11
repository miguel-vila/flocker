# Flocker

Un generador de tweets usando una cadena de markov sencilla. Se alimenta con una lista de usuarios de twitter y se producen tweets basados en los tweets de esos usuarios. 

Usa:

* Actores de Akka.
* Redis para almacenar bigramas de palabras producidas por los usuarios .
* MongoDB para persistir el estado de algunos actores.

Todavía en estado beta: **nada usable todavía**.

##Application.conf

El archivo de `application.conf` debería verse algo como:

```hocon
twitter {
  OAuthConsumerKey = "yourOAuthConsumerKey"
  OAuthConsumerSecret = "yourOAuthConsumerSecret"
  OAuthAccessToken = "yourOAuthAccessToken"
  OAuthAccessTokenSecret = "yourOAuthAccessTokenSecret"
}

akka.persistence.journal.plugin = "casbah-journal"
```

##TO-DO
- [ ] Límitar la generación de texto (tweets) a que sea menor a 140 caractéres.
- [ ] Twittear el texto generado.
- [ ] Proveer un API REST / HTTP para facilidad de uso.
