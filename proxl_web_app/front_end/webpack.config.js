
//  webpack.config.js

const path = require('path');
const webpack = require('webpack');

	//  https://www.npmjs.com/package/case-sensitive-paths-webpack-plugin
var CaseSensitivePathsPlugin = require('case-sensitive-paths-webpack-plugin');

//  Extracts the compiled SCSS into a standalone .css file.
//  Replaces the old file-loader + extract-loader chain (extract-loader pulled in
//  the legacy babel-core@6 stack, the source of most npm-audit findings).
const MiniCssExtractPlugin = require('mini-css-extract-plugin');


//  No longer used
// const HandlebarsPrecompiler = require('webpack-handlebars-precompiler');
		
     //  Also removed from package.json      "webpack-handlebars-precompiler": "^1.1.0"


const mainConfig = {

	//  Browser target — ONLY governs the ES level of webpack's own runtime/bootstrap
	//  code (the chunk-loading / module-wrapper glue webpack generates).
	//  There is NO transpiler (Babel/swc/esbuild) wired into this build, so application
	//  source is NOT downleveled — it ships as authored. This just tells webpack it may
	//  rely on syntax supported by browsers from roughly the last 2 years.
	//  Inline browserslist query, so no .browserslistrc / package.json "browserslist" is needed.
	target: 'browserslist:last 2 years, not dead',

	devtool: 'source-map',
	resolve: {
	    alias: { // A trailing $ can also be added to the given object's keys to signify an exact match:
	       'handlebars.runtime$': 'handlebars/dist/handlebars.runtime.min.js',
	       'handlebars$': 'handlebars/dist/handlebars.min.js',
			fs: 'pdfkit/js/virtual-fs.js'
	    },
	    modules: [
	        path.resolve('./src/js'),
	        //  Relative 'node_modules' (not an absolute path) so webpack walks up the
	        //  tree and can find nested deps (e.g. stream-browserify's readable-stream@3).
	        'node_modules',
		],
		//  webpack 5 no longer auto-polyfills Node core modules.
		//  pdfkit / blob-stream / fontkit need these provided explicitly.
		//   https://github.com/foliojs/pdfkit/tree/master/examples/webpack
		fallback: {
			fs: require.resolve('pdfkit/js/virtual-fs.js'),
			assert: require.resolve('assert/'),
			buffer: require.resolve('buffer/'),
			events: require.resolve('events/'),
			stream: require.resolve('stream-browserify'),
			util: require.resolve('util/'),
			zlib: require.resolve('browserify-zlib'),
			crypto: false,
		},
	},
	plugins: [
		//  webpack 5: Buffer / process are no longer globals
		new webpack.ProvidePlugin({
			Buffer: ['buffer', 'Buffer'],
			process: 'process/browser',
		}),
		//  Emit the SCSS as css_generated/global.css (the 'styles' entry == global.scss).
		//  Keeps the historical filename so JSP <link> references are unchanged.
		new MiniCssExtractPlugin({
			filename: (pathData) =>
				pathData.chunk.name === 'styles' ? 'css_generated/global.css' : 'css_generated/[name].css',
		}),
		new CaseSensitivePathsPlugin()
		//  Removed (both) since doesn't precompile the Handlebars in an output format that can be imported  
//        new HandlebarsPrecompiler({
//            precompileOpts: {preventIndent: true},  //  Passed to Handlebars.precompile(..., precompileOpts ); https://handlebarsjs.com/reference.html
//            templatesPath: path.join(__dirname, 'handlebars_templates'),
//            templatesExt: '.hbs',
////            helpersPath: path.join(__dirname, 'helpers'), // optional
//            outputFile: path.join(__dirname, 'handlebars_templates_precompiled/bundle.js'),
//        }),
//        new HandlebarsPrecompiler( {
//        	precompileOpts: { preventIndent: true }, //  Passed to Handlebars.precompile(..., precompileOpts ); https://handlebarsjs.com/reference.html
//        	templatesPath: path.join( __dirname, 'handlebars_templates','peptide_table' ), 
//        	templatesExt: '.handlebars',
////      	helpersPath: path.join(__dirname, 'helpers'), // optional
//        	outputFile: path.join( __dirname, 'handlebars_templates_precompiled', 'peptide_page', 'peptide_table_template-bundle.js' )
//        } )
    ],

	entry: {
		
		//  header_section_every_page

		'header_section_every_page/header_section_every_page' : './src/js/page_js/header_section_js_all_pages_main_pages/header_section_every_page/header_section_every_page_root.js',
		

		//  header_section_main_pages

		'header_section_main_pages/header_section_main_pages' : './src/js/page_js/header_section_js_all_pages_main_pages/header_section_main_pages/header_main.js',
		
		//  Data Pages
		//
		
		//  Projects List  Page:
		
		'data_pages/projectsListPage' : './src/js/page_js/data_pages/project_list_page/listProjectsPage.js',
		
		//  Project Page:
		//   Project page for each type of user and whether or not the project is locked
		//   Applies to both
		'data_pages/projectViewPage_PublicUser' : './src/js/page_js/data_pages/project_page/projectPage_Root_PublicUser.js',
		//   Project is NOT Locked
		'data_pages/projectViewPage_Researcher_W_User' : './src/js/page_js/data_pages/project_page/projectPage_Root_ResearcherUser.js',
		'data_pages/projectViewPage_ProjectOwner_W_User' : './src/js/page_js/data_pages/project_page/projectPage_Root_ProjectOwnerUser.js',
		//   Project is Locked
		'data_pages/projectViewPage_ProjectLocked_Researcher_W_User' : './src/js/page_js/data_pages/project_page/projectPage_Root_ProjectLocked_ResearcherUser.js',
		'data_pages/projectViewPage_ProjectLocked_ProjectOwner_W_User' : './src/js/page_js/data_pages/project_page/projectPage_Root_ProjectLocked_ProjectOwnerUser.js',
		
		//  ProjectSearchId Driven pages

		//  Single Search (peptide,protein[cross/loop/all])
		'data_pages/peptideView' : './src/js/page_js/data_pages/project_search_ids_driven_pages/peptide_page/viewSearchPeptide.js',
		'data_pages/crosslinkProteinView' : './src/js/page_js/data_pages/project_search_ids_driven_pages/protein_pages/viewSearchCrosslinkProteinPage.js',
		'data_pages/looplinkProteinView' : './src/js/page_js/data_pages/project_search_ids_driven_pages/protein_pages/viewSearchLooplinkProteinPage.js',
		'data_pages/proteinsAllView' : './src/js/page_js/data_pages/project_search_ids_driven_pages/protein_pages/viewSearchProteinAllPage.js',

		'data_pages/qcView' : './src/js/page_js/data_pages/project_search_ids_driven_pages/qc_page/qcPageMain.js',

		//  Merged Searches (peptide,protein[cross/loop/all])
		'data_pages/peptideMergedView' : './src/js/page_js/data_pages/project_search_ids_driven_pages/peptide_page/viewMergedPeptide.js',
		'data_pages/proteinCrosslinkMergedView' : './src/js/page_js/data_pages/project_search_ids_driven_pages/protein_pages/viewMergedCrosslinkProteinPage.js',
		'data_pages/proteinLooplinkMergedView' : './src/js/page_js/data_pages/project_search_ids_driven_pages/protein_pages/viewMergedLooplinkProteinPage.js',
		'data_pages/proteinAllMergedView' : './src/js/page_js/data_pages/project_search_ids_driven_pages/protein_pages/viewMergedProteinAllPage.js',
		
		'data_pages/qcMergedView' : './src/js/page_js/data_pages/project_search_ids_driven_pages/qc_page/qcMergedPageMain.js',
		
		//  Shared Single Search and Merged Searches
		
		'data_pages/proteinCoverageReportView' : './src/js/page_js/data_pages/project_search_ids_driven_pages/protein_coverage/viewProteinCoverageReport.js',
		
		'data_pages/imageView' : './src/js/page_js/data_pages/project_search_ids_driven_pages/image_page/crosslink-image-viewer.js',
		'data_pages/structureView' : './src/js/page_js/data_pages/project_search_ids_driven_pages/structure_page/structure-viewer-page.js',
	
		////////////////
		
		//  Proxl Config Page
		
		'proxl_config_page/configureProxlForAdminPage' : './src/js/page_js/proxl_config_page/configureProxlForAdminPage.js',

		//  User Pages
		'user_pages/userLoginPage' : './src/js/page_js/user_account_page_js/userLoginPage.js',
		'user_pages/manageUsersPage' : './src/js/page_js/user_account_page_js/manageUsersPage.js',
		'user_pages/accountManagementPage' : './src/js/page_js/user_account_page_js/accountManagementPage.js',
		'user_pages/inviteUserGetNewUserInfo' : './src/js/page_js/user_account_page_js/inviteUserGetNewUserInfo.js',
		'user_pages/userResetPasswordChangePasswordPage' : './src/js/page_js/user_account_page_js/userResetPasswordChangePasswordPage.js',
		'user_pages/userResetPasswordPage' : './src/js/page_js/user_account_page_js/userResetPasswordPage.js',
		'user_pages/userSignup' : './src/js/page_js/user_account_page_js/userSignup.js',

		
		// SASS files
		'styles' : './src/styles/global.scss',

	},
	output: {
		path: path.resolve(__dirname, 'webpack_build_output/'),
		filename: 'js_generated_bundles/[name]-bundle.js'
	},

	module:{
		rules:[
			
			//  Upgrade pdfkit to webpack 5
			
			//   https://github.com/foliojs/pdfkit/tree/master/examples/webpack
			//   https://github.com/blikblum/pdfkit-webpack-example/issues/9#issuecomment-754876467
			//   https://github.com/foliojs/pdfkit/issues/1195
			
			// for pdfkit
			{
				enforce: 'post',
				test: /fontkit[/\\]index.js$/,
				use: {
					loader: "transform-loader?brfs"
				}
			},
			// for pdfkit
			{
				enforce: 'post',
				test: /unicode-properties[/\\]index.js$/,
				use: {
					loader: "transform-loader?brfs"
				}
			},
			// for pdfkit
			{
				enforce: 'post',
				test: /linebreak[/\\]src[/\\]linebreaker.js/,
				use: {
					loader: "transform-loader?brfs"
				}
			},
//			{ enforce: 'post', test: /fontkit[/\\]index.js$/, loader: "transform-loader?brfs" },				
//			{ enforce: 'post', test: /unicode-properties[/\\]index.js$/, loader: "transform-loader?brfs" },		// for pdfkit
//			{ enforce: 'post', test: /linebreak[/\\]src[/\\]linebreaker.js/, loader: "transform-loader?brfs" },	// for pdfkit

			{
				//  Output .css file is NOT Minimized
				test:/\.scss$/,
				use: [
					{
						loader: MiniCssExtractPlugin.loader
					},
					{
						loader: 'css-loader'
					},
					{
						loader: 'sass-loader',
						//  Use dart-sass modern API (legacy JS API is deprecated, removed in Dart Sass 2.0)
						options: { api: 'modern' }
					}
				]
			}
		 	]
	  },
};

module.exports = mainConfig;

//     Following doesn't work.

//  export default [ mainConfig ];

