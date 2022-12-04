var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle1="mensaje"]'))
var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl){
return new bootstrap.Tooltip(tooltipTriggerEl)
})