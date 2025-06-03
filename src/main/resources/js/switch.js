function switchText() {
  var checkBox = document.getElementById("fullTxtChx");
  const collt = document.getElementsByClassName("slottext");
  for (let i = 0; i < collt.length; i++) {
    if (checkBox.checked == true){
      collt.item(i).style.display = "inline";
      collt.item(i).style.gridColumn = "span 6";
    } else {
      collt.item(i).style.display = "none";
      collt.item(i).style.gridColumn = "";
    }
  }
  const colli = document.getElementsByClassName("sloticon");
  for (let i = 0; i < colli.length; i++) {
    if (checkBox.checked == true){
      colli.item(i).style.gridColumn = "2";
    } else {
      colli.item(i).style.gridColumn = "";
    }
  }
}