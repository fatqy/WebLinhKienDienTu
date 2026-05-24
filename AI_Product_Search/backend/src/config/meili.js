const { MeiliSearch } = require('meilisearch');
require('dotenv').config();

const client = new MeiliSearch({
  host: process.env.MEILI_HOST || 'http://localhost:7700',
  apiKey: process.env.MEILI_MASTER_KEY || 'aSampleMasterKey',
});

const initMeiliSearch = async () => {
  try {
    const index = client.index('products');
    
    // Setting up searchable attributes for fuzzy search
    await index.updateSearchableAttributes([
      'name',
      'brand',
      'category',
      'description',
      'tags'
    ]);

    // Setting up filterable attributes
    await index.updateFilterableAttributes([
      'brand',
      'category',
      'price',
      'rating',
      'inStock'
    ]);
    
    // Custom ranking rules for product score
    await index.updateRankingRules([
      'words',
      'typo',
      'proximity',
      'attribute',
      'sort',
      'exactness',
      'rating:desc',
      'salesCount:desc'
    ]);

    console.log('Meilisearch settings configured successfully.');
  } catch (error) {
    console.error('Error configuring Meilisearch:', error);
  }
};

module.exports = { client, initMeiliSearch };