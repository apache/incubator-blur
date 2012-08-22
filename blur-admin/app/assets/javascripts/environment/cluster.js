var ClusterModel = Backbone.Model.extend({
  initialize: function(){
    this.view = new ClusterView({model: this});
    this.on('change', function(){
      this.view.render();
    });
  },
  safe_mode: function(){
    return this.get('safe_mode') ? 'Yes' : 'No';
  }
});

var ClusterCollection = Backbone.StreamCollection.extend({
  model: ClusterModel,
  initialize: function(models, options){
    this.view = new ClusterCollectionView().render();
    this.on('add', function(cluster){
      var container = this.view.$el.find('tbody');
      container.append(cluster.view.render().$el);
    });
  }
});

var ClusterCollectionView = Backbone.View.extend({
  tagName: 'table',
  className: 'table table-bordered',
  template: JST['templates/environment/cluster_collection'],
  render: function(){
    this.$el.html(this.template());
    return this;
  }
});

var ClusterView = Backbone.View.extend({
  tagName: 'tr',
  template: JST['templates/environment/cluster'],
  events:{
    "click .more-shard-info" : "show_shards"
  },
  render: function(){
    this.$el.attr('data-cluster-id', this.model.get('id')).html(this.template({cluster: this.model}));
    return this;
  },
  show_shards: function(event){
    new ShardCollection(null, {cluster_id: this.model.get('id')});
  }
});