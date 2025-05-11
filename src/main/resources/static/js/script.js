document.addEventListener('DOMContentLoaded', function () {
  console.log('Página cargada correctamente');
});
// Función para mostrar los Términos y Condiciones
document.querySelector('.terms-link').addEventListener('click', function() {
  document.querySelector('.terms-container').style.display = 'block';
});

// Función para cerrar el modal
function closeTerms() {
  document.querySelector('.terms-container').style.display = 'none';
}
// Función que maneja el inicio de sesión exitoso
function onSignIn(googleUser) {
  var profile = googleUser.getBasicProfile();
  var id_token = googleUser.getAuthResponse().id_token;

  // Muestra el nombre del usuario (para pruebas)
  document.getElementById('status').innerHTML = 'Hola, ' + profile.getName();

  // Redirige al panel del cliente o donde quieras redirigir al usuario
  window.location.href = 'panel.html'; // Puedes cambiar a otra página de tu elección
}
function signOut() {
  var auth2 = gapi.auth2.getAuthInstance();
  auth2.signOut().then(function () {
    console.log('Usuario desconectado.');
    document.getElementById('access-panel-btn').style.display = 'none';  // Esconde el botón cuando cierre sesión
  });
}
// Este script se debe ejecutar después de que el fotógrafo haya iniciado sesión con Google
function onSignIn(googleUser) {
  // Si el usuario es un fotógrafo autenticado, mostramos el botón
  var profile = googleUser.getBasicProfile();
  var email = profile.getEmail();  // Aquí puedes comparar el correo electrónico del fotógrafo
  if (email === 'sayaline.ik@gmail.com') {  // Reemplaza esto con el correo del fotógrafo
    document.getElementById('access-panel-btn').style.display = 'block';  // Muestra el botón
  }
}
// Temporariamente mostrar el botón sin autenticación de Google
document.getElementById('access-panel-btn').style.display = 'block';  // Muestra el botón
