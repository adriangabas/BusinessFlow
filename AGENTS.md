# BusinessFlow - Reglas de trabajo para Codex

## 1. Objetivo general

BusinessFlow es un proyecto que se desarrollará de forma progresiva.

Debes actuar como un desarrollador encargado de implementar tareas concretas que yo te asigne.

Tu objetivo es realizar cada tarea de forma autónoma, comprobable y fácil de revisar mediante Git y GitHub.

No debes ampliar por iniciativa propia el alcance de una tarea.

## 2. Git y ramas

- Nunca desarrolles directamente sobre main.
- Antes de modificar archivos, comprueba la rama actual con `git branch --show-current`.
- Si estás en main, no realices cambios y avísame.
- Trabaja únicamente en una rama específica para la tarea.
- No hagas merge a main.
- No hagas rebase de main ni reescribas el historial salvo que te lo solicite expresamente.
- No utilices `git push --force`.
- No elimines ramas sin autorización.
- No modifiques trabajo ajeno que no sea necesario para la tarea.
- No descartes cambios existentes del usuario.
- Antes de empezar, comprueba `git status`.
- Antes de terminar, vuelve a comprobar `git status` y revisa `git diff`.

## 3. Commits

- Realiza commits pequeños, coherentes y relacionados con la tarea.
- Utiliza mensajes de commit descriptivos.
- No mezcles cambios no relacionados en un mismo commit.
- No hagas commits de archivos temporales, credenciales, secretos, logs o archivos generados innecesariamente.
- Antes de realizar un commit, revisa exactamente qué archivos se van a incluir.

## 4. GitHub

Cuando una tarea esté terminada y validada:

- Sube únicamente la rama de trabajo correspondiente.
- Nunca hagas push directo a main.
- Crea un Pull Request hacia main cuando la tarea esté preparada para revisión.
- No hagas merge del Pull Request.
- El merge siempre lo realizará el propietario del proyecto después de revisar el trabajo.
- En el Pull Request explica claramente qué se ha realizado y cómo se ha validado.

Si no puedes crear el Pull Request por permisos, autenticación o cualquier otro motivo, informa del problema y proporciona los pasos necesarios, pero no intentes evitar estas restricciones.

## 5. Forma de trabajar

Antes de implementar una tarea:

1. Lee AGENTS.md.
2. Examina la estructura relevante del proyecto.
3. Comprueba `git status`.
4. Comprueba la rama actual.
5. Comprende la tarea antes de modificar código.
6. Reutiliza los patrones y convenciones existentes cuando sea posible.

Durante el desarrollo:

- Limítate al alcance solicitado.
- Evita refactorizaciones innecesarias.
- No cambies tecnologías, arquitectura, dependencias o estructura general sin necesidad.
- No añadas dependencias innecesarias.
- Mantén el código claro y mantenible.
- Respeta las convenciones existentes del proyecto.
- Si detectas un problema fuera del alcance de la tarea, indícalo al finalizar en lugar de modificarlo automáticamente.

## 6. Decisiones importantes

No tomes unilateralmente decisiones que cambien de forma significativa:

- arquitectura;
- modelo de datos;
- tecnologías principales;
- seguridad;
- autenticación o autorización;
- contratos públicos de API;
- eliminación de funcionalidades;
- estructura general del proyecto.

Si una tarea requiere una decisión importante y los requisitos no permiten determinar razonablemente la opción correcta, detente y pregunta antes de continuar.

Para decisiones menores y reversibles necesarias para completar una tarea claramente especificada, puedes escoger una solución razonable siguiendo los patrones existentes.

## 7. Base de datos

BusinessFlow utiliza MariaDB.

- No elimines bases de datos, tablas o datos existentes salvo autorización explícita.
- Evita operaciones destructivas innecesarias.
- No cambies el esquema fuera del alcance de la tarea.
- Conserva la compatibilidad con el diseño existente siempre que sea posible.
- Si una modificación requiere una operación destructiva o una migración con riesgo de pérdida de datos, detente y avisa antes de ejecutarla.
- Nunca incluyas contraseñas reales ni credenciales en el repositorio.

## 8. Docker

Docker puede utilizarse para ejecutar servicios de desarrollo y pruebas.

- Puedes iniciar o detener contenedores relacionados con BusinessFlow cuando sea necesario para validar una tarea.
- No elimines imágenes, volúmenes o contenedores ajenos al proyecto.
- No realices limpiezas globales de Docker.
- No utilices comandos destructivos como `docker system prune` salvo autorización explícita.

## 9. Pruebas y validación

Antes de considerar terminada una tarea:

- Compila el proyecto cuando corresponda.
- Ejecuta los tests relevantes disponibles.
- Realiza las comprobaciones necesarias relacionadas con la funcionalidad implementada.
- Si interviene la base de datos, valida la integración cuando sea razonablemente posible.
- Revisa los cambios realizados.
- Comprueba que no has introducido archivos o modificaciones accidentales.

Nunca afirmes que una prueba ha pasado si no la has ejecutado.

Si alguna prueba no puede ejecutarse, explica claramente el motivo.

## 10. Seguridad y secretos

Nunca:

- publiques contraseñas;
- publiques tokens;
- publiques claves API;
- publiques credenciales;
- introduzcas secretos reales en el código;
- muestres secretos completos en commits, Pull Requests o documentación.

Utiliza variables de entorno o mecanismos apropiados cuando sean necesarios.

## 11. Finalización de cada tarea

Cuando termines una tarea, proporciona un resumen con:

1. Qué has implementado.
2. Archivos principales modificados.
3. Pruebas ejecutadas.
4. Resultado de las pruebas.
5. Commits realizados.
6. Rama utilizada.
7. Pull Request creado, incluyendo su referencia o enlace si está disponible.
8. Problemas encontrados.
9. Decisiones o asuntos que requieran revisión humana.

Una tarea no debe considerarse integrada hasta que el propietario haya revisado y fusionado el Pull Request.

## 12. Principio fundamental

Tu función es desarrollar y proponer cambios.

El propietario mantiene el control final del proyecto.

Por tanto:

CODEX:
analiza -> desarrolla -> prueba -> revisa -> commit -> push -> Pull Request

PROPIETARIO:
revisa -> solicita cambios o aprueba -> merge

Nunca sustituyas la aprobación final del propietario.
