

function convertColor_HexToRGB_ResultObject(hex){
    hex = hex.replace('#','');
    r = parseInt(hex.substring(0,2), 16);
    g = parseInt(hex.substring(2,4), 16);
    b = parseInt(hex.substring(4,6), 16);

    result = 'rgb(' + r + ',' + g + ',' + b + ',' + ')';
    return result;
}