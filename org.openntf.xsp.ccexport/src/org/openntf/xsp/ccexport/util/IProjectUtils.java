package org.openntf.xsp.ccexport.util;

import java.util.ArrayList;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;

/**
 * Méthodes utiles pour gérer les projets (au sens large)
 * @author Lionel HERVIER
 */
public class IProjectUtils {

	/**
	 * Retourne un projet à partir de son nom
	 * @param name le nom du projet
	 * @return le projet
	 */
	public static IProject getProjectFromName(String name) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		return root.getProject(name);
	}
	
	/**
	 * Pour savoir si un projet est d'une nature donnée
	 * @param project le projet
	 * @param natureId l'ID de la nature
	 * @return true si le projet est de cette nature
	 * @throws CoreException 
	 */
	public static boolean hasNature(IProject project, String natureId) {
		String[] natures;
		try {
			natures = project.getDescription().getNatureIds();
		} catch (CoreException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		}
		for( String nature : natures ) {
			if( natureId.equals(nature) )
				return true;
		}
		return false;
	}
	
	/**
	 * Ajoute un builder à un projet
	 * @param project le projet auquel ajouter le builder
	 * @param builderId l'id du builder
	 * @param monitor le moniteur
	 * @throws CoreException en cas de problème
	 */
	public static void addBuilderToProject(
			IProject project, 
			String builderId,
			IProgressMonitor monitor) throws CoreException {
		// Vérifie que le projet existe
		if( project == null || !project.exists() )
			return;
		
		// Récupère les builders déjà associés, et vérifie que le nôtre n'est pas déjà dedans
		IProjectDescription projectDesc = project.getDescription();
		ICommand[] initBuildSpec = projectDesc.getBuildSpec();
		for( int j = 0; j < initBuildSpec.length; j++ ) {
			if (builderId.equals(initBuildSpec[j].getBuilderName()))
				return;
		}
		
		// Ajoute notre builder
		ICommand command = projectDesc.newCommand();
		command.setBuilderName(builderId);
		ICommand[] newBuildSpec = new ICommand[initBuildSpec.length + 1];
		System.arraycopy(initBuildSpec, 0, newBuildSpec, 1, initBuildSpec.length);
		newBuildSpec[0] = command;
		projectDesc.setBuildSpec(newBuildSpec);
		project.setDescription(projectDesc, monitor);
	}
	
	/**
	 * Enlève un builder d'un projet
	 * @param project le projet
	 * @param builderId l'id du builder
	 * @throws CoreException en cas de pb
	 */
	public static void removeBuilderFromProject(IProject project, String builderId) throws CoreException {
		if ((project == null) || (!project.exists()))
			return;
		
		IProjectDescription desc = project.getDescription();
		ICommand[] commands = desc.getBuildSpec();
		ArrayList<ICommand> newCommands = new ArrayList<ICommand>();
		boolean bFound = false;
		for( int i = 0; i < commands.length; i++ ) {
			if( builderId.equals(commands[i].getBuilderName()) )
				bFound = true;
			else
				newCommands.add(commands[i]);
		}
		if( !bFound )
			return;
		ICommand[] newCommandArray = (ICommand[]) newCommands.toArray(new ICommand[0]);
		desc.setBuildSpec(newCommandArray);
		project.setDescription(desc, new NullProgressMonitor());
	}
	
	/**
	 * Créé un dossier
	 * @param project le projet
	 * @param folderPath le chemin vers le dossier
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * 		to call done() on the given monitor. Accepts null, indicating that no progress should be
	 * 		reported and that the operation cannot be cancelled.
	 * @throws CoreException en cas de pb
	 */
	public static void createFolder(IProject project, IPath folderPath, IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor);
		
		String[] segments = folderPath.segments();
		IPath curr = null;
		for( int i=0; i<segments.length; i++ ) {
			progress.setWorkRemaining(10000);
			if( i == 0 )
				curr = new Path(segments[0]);
			else
				curr = curr.append(segments[i]);
			IFolder folder = project.getFolder(curr);
			if( !folder.exists() )
				folder.create(true, true, progress.newChild(1));
		}
	}
}
