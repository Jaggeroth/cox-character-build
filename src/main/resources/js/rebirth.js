function compareValues(key, order = 'asc') {
  return function innerSort(a, b) {
    if (!a.hasOwnProperty(key) || !b.hasOwnProperty(key)) {
      // property doesn't exist on either object
      return 0;
    }

    var varA = (typeof a[key] === 'string')
      ? a[key].toUpperCase() : a[key];
    var varB = (typeof b[key] === 'string')
      ? b[key].toUpperCase() : b[key];
	if (varA === varB) {
	  varA = a.name.toUpperCase();
	  varB = b.name.toUpperCase();
	}
    let comparison = 0;
    if (varA > varB) {
      comparison = 1;
    } else if (varA < varB) {
      comparison = -1;
    }
    return (
      (order === 'desc') ? (comparison * -1) : comparison
    );
  };
}

function characterText(charData) {
  return ' (' + charData.level + ') ' + charData.name;
}

function getOriginImage(origin) {
  let img = new Image();
  img.title = initCap(origin);
  var srcTxt = 'images\\origin\\' + origin.toLowerCase() + '.png';
  img.src = srcTxt;
  img.width=32;
  img.height=32;
  return img;
}

function getAlignmentImage(alignment) {
  let img = new Image();
  img.title = initCap(alignment);
  var srcTxt = 'images\\alignment\\' + alignment.toLowerCase() + '.png';
  img.src = srcTxt;
  img.width=32;
  img.height=32;
  return img;
}

function getAtImage(archetype) {
  let img = new Image();
  img.title = initCap(archetype);
  var srcTxt = 'images\\archetype\\' + archetype.toLowerCase() + '.png';
  img.src = srcTxt;
  img.width=32;
  img.height=32;
  return img;
}

function replaceList(ul, charData) {
  for(var i = 0; i < charData.length; i++) {
    var li_elem = document.createElement("li");
	li_elem.setAttribute('title',charData[i].title);
	var link = document.createElement('a');
	link.appendChild(document.createTextNode(' '));
	link.appendChild(getOriginImage(charData[i].origin));
	link.appendChild(document.createTextNode(' '));
	link.appendChild(getAlignmentImage(charData[i].alignment));
	link.appendChild(document.createTextNode(' '));
	link.appendChild(getAtImage(charData[i].at));
	link.appendChild(document.createTextNode(' '));
    var nl = document.createTextNode(characterText(charData[i]));
	link.appendChild(nl);
	link.setAttribute('href', charData[i].url + '.html');
	li_elem.appendChild(link);
	var ol = ul.children[i];
	ol.replaceChild(li_elem, ol.childNodes[0]);
  }
}

function initCap(value) {
  return value.charAt(0).toUpperCase() + value.slice(1)
}

function reloadChars() {
  let key = document.getElementById("sortOrder").value;
  let order = document.getElementById("ascDesc").value;
  characters.sort(compareValues(key,order));
  var uls = document.getElementsByTagName('ul')[0];
  replaceList(uls, characters);
}