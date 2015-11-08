$(document).ready(function(){
  statsPage.init();
});


var statsPage = {
  init: function(){
      statsPage.initStyling();
      statsPage.initEvents();
    },
  initStyling: function(){
    statsPage.grabStatsFromServer();

  },
  initEvents: function(){
    //*****Login page that will then bring up Home page - THECLICKHIDDENFUNCTION****
    $('#stateYearButton').on('click', function(event){
      console.log("this is happening - initialbutton");
      event.preventDefault();
      // $('.statMain').toggleClass('hidden');
      var $state = $('input[name="state"]').val();
      var $year = $('input[year="year"]').val();
      // statsPage.setState($state);
      // statsPage.setYear($year);
      _.each(stats, function(currVal, idx, array) {
        if($state === currVal.state && $year === currVal.year) {
          statsPage.loadStats(currVal);
        } else {
          statsPage.grabsStatsFromServer();
        }
      });
    });
  },

  grabStatsFromServer: function() {
    $.ajax({
      method: 'GET',
      url: '/',
      success: function(crime) {
        console.log("SUCCESS: ");
        window.crimeData = JSON.parse(crime);
        var national = _.filter(crime, function(el, idx, arr) {
          // return el.name === "National" && el.year === "2012";
          // return el.na === "National";
          return el.population === 313914040;
        });
        // var nationalData = _.map(national, function(el, idx, arr){
        //   console.log(el.name);
        // });
        console.log(national);
        statsPage.loadStats(national);
      },
      failure: function(crime) {
        console.log("FAILURE: ", crime);
      }
    });
  },
//pppp
  //once we GET chats we have to load into template here
  loadStats: function(data) {
    var statsHTML = "";
        var statsTemplateCurrUser = _.template($('#statsTmplCurrUser').html());
        statsHTML += statsTemplateCurrUser(data);
        $('.statMain').html(statsHTML);
  },


  url: "/",

};
