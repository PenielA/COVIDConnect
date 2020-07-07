 function log_button(){
     fetch('/logServlet').then(response => response.json()).then((logUrl) => {
     console.log(logUrl);
     document.getElementById("loglink").href = logUrl[1];
     document.getElementById("loglink").innerText = logUrl[0];
      });
 }