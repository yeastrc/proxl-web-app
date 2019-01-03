
//  webpack.config.js

const path = require('path');

//  Parallel Webpack from  https://github.com/trivago/parallel-webpack


	//  https://www.npmjs.com/package/case-sensitive-paths-webpack-plugin
var CaseSensitivePathsPlugin = require('case-sensitive-paths-webpack-plugin');


//  No longer used
// const HandlebarsPrecompiler = require('webpack-handlebars-precompiler');
		
     //  Also removed from package.json      "webpack-handlebars-precompiler": "^1.1.0"


const mainConfig = {		
	
	devtool: 'source-map',
	resolve: {
	    alias: {
	       'handlebars.runtime': 'handlebars/dist/handlebars.runtime.min.js'
	    },
	    modules: [
	        path.resolve('./src/js'),
	        path.resolve('./node_modules'),
		]
	},
	plugins: [
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

		'header_section_every_page/header_section_every_page' : './src/js/page_js/common_all_pages/header_section_every_page/header_section_every_page_root.js',
		

		//  header_section_main_pages

		'header_section_main_pages/header_section_main_pages' : './src/js/page_js/common_all_pages/header_section_main_pages/header_main.js',
		
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
		// 'styles' : './src/styles/global.scss',

	},
	output: {
		path: path.resolve(__dirname, 'webpack_build_output/'),
		filename: 'js_generated_bundles/[name]-bundle.js'
	},

	module:{
		rules:[
				{
					test:/\.scss$/,
					use: [
						{
							loader: 'file-loader',
							options: {
								name: '[name].css',
								outputPath: 'css_generated/'
							}
						},
						{
							loader: 'extract-loader'
						},
						{
							loader: 'css-loader',
							options: { minimize: true }
						},
						{
							loader: 'sass-loader'
						}
					]
				}
		 	]
	  },
};

module.exports = mainConfig;

//     Following doesn't work.

//  export default [ mainConfig ];

